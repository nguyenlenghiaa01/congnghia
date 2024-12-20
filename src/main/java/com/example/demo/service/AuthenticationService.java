package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.Enum.Role;
import com.example.demo.exception.AuthException;
import com.example.demo.exception.DuplicateEntity;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.*;
import com.example.demo.model.Request.*;
import com.example.demo.model.Response.AccountResponse;
import com.example.demo.model.Response.DataResponse;
import com.example.demo.model.Response.UserResponse;
import com.example.demo.repository.AccountRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Service
@Lazy
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;


    public AccountResponse register(RegisterRequest registerRequest) {
        Account account = modelMapper.map(registerRequest, Account.class);
        try {
            String originPassword = account.getPassword();
            account.setPassword(passwordEncoder.encode(originPassword));
            //generate code account
            String getAccountCode;
            do {
                getAccountCode = generateAccountCode(account.getRole());
            } while (accountRepository.findAccountByCode(getAccountCode) != null);
            account.setCode(getAccountCode);
            account.setImage("");
            Account newAccount = accountRepository.save(account);
//            String otp = otpService.generateOtp();
//            emailService.sendOtp(account.getEmail(), otp);
//            // gui mail
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setReceiver(newAccount);
            emailDetail.setSubject("WelCome");
            emailDetail.setLink("https://www.ansovatravel.com/send-an-inquiry/");// ve trang home page
            emailService.sendEmail(emailDetail);
            return modelMapper.map(newAccount, AccountResponse.class);
        } catch (Exception e) {
            if (e.getMessage().contains(account.getUsername())) {
                throw new DuplicateEntity("Duplicate username!");
            } else if (e.getMessage().contains(account.getEmail())) {
                throw new DuplicateEntity("Duplicate email!");
            } else if (e.getMessage().contains(account.getPhone())) {
                throw new DuplicateEntity("Duplicate phone!");
            } else {
                System.out.println(e.getMessage());
                throw e;
            }
        }
    }

    @Autowired
    AuthenticationManager authenticationManager;

    public AccountResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getUserName(),
                    loginRequest.getPassword()
            ));
            // tai khoan co ton tai
            Account account = (Account) authentication.getPrincipal();
            AccountResponse accountResponse = modelMapper.map(account, AccountResponse.class);
            accountResponse.setToken(tokenService.generateToken(account));
            return accountResponse;
        } catch (Exception e) {
            throw new EntityNotFoundException("User name or password is invalid !");
        }
    }

    public DataResponse<AccountResponse> getAllAccount(@RequestParam int page,@RequestParam int size) {
        Page accountPage = accountRepository.findAll(PageRequest.of(page, size));
        List<Account> accounts = accountPage.getContent();
        List<AccountResponse> accountResponses = new ArrayList<>();
        for(Account account: accounts) {
            if(!account.isDeleted()) {
                AccountResponse accountResponse = new AccountResponse();
                accountResponse.setId(account.getId());
                accountResponse.setCode(account.getCode());
                accountResponse.setAddress(account.getAddress());
                accountResponse.setPhone(account.getPhone());
                accountResponse.setFullName(account.getFullName());
                accountResponse.setEmail(account.getEmail());
                accountResponse.setRole(account.getRole());
                accountResponse.setImage(account.getImage());

                accountResponses.add(accountResponse);
            }
        }

        DataResponse<AccountResponse> dataResponse = new DataResponse<AccountResponse>();
        dataResponse.setListData(accountResponses);
        dataResponse.setTotalElements(accountPage.getTotalElements());
        dataResponse.setPageNumber(accountPage.getNumber());
        dataResponse.setTotalPages(accountPage.getTotalPages());
        return dataResponse;
    }

    public Map<String, Integer> getAllAccountByRole() {
        List<Account> accounts = accountRepository.findAll();
        int cusCount = 0;
        int saleCount = 0;
        int consultingCount = 0;
        int managerCount=0;

        for (Account account : accounts) {
            if (account.getRole() == Role.CUSTOMER) {
                cusCount++;
            } else if (account.getRole() == Role.SALE) {
                saleCount++;
            } else if (account.getRole() == Role.CONSULTING) {
                consultingCount++;
            } else if(account.getRole() == Role.MANAGER){
                managerCount++;
            }
        }
        Map<String, Integer> roleCounts = new HashMap<>();
        roleCounts.put("CUSTOMER", cusCount);
        roleCounts.put("SALE", saleCount);
        roleCounts.put("CONSULTING", consultingCount);
        roleCounts.put("MANAGER",managerCount);

        return roleCounts;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Account account = accountRepository.findAccountByUserName(userName);
        if (account == null) {
            throw new UsernameNotFoundException("Account with username " + userName + " not found");
        }
        return account;
    }

    public Account deleteAccount(long id){
        Account oldAccount = accountRepository.findAccountById(id);
        if(oldAccount ==null){
            throw new NotFoundException("Account not found !");//dung viec xu ly ngay tu day
        }
        oldAccount.setDeleted(true);
        return accountRepository.save(oldAccount);
    }

    public Account updateAccount(Long Id, AccountUpdateRequest newAccount) {
        Account oldAccount = accountRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        oldAccount.setEmail(newAccount.getEmail());
        oldAccount.setPhone(newAccount.getPhone());
        oldAccount.setFullName(newAccount.getFullName());
        oldAccount.setAddress(newAccount.getAddress());
        oldAccount.setImage(newAccount.getImage());

        return accountRepository.save(oldAccount);
    }

    public Account getCurrentAccount(){
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountRepository.findAccountByCode(account.getCode());
    }

    //extensions
    public String generateAccountCode(Role role) {
        String accountCode;
        String prefix = role.name().substring(0, 3).toUpperCase();

        Random random = new Random();
        int randomNumber = 100000 + random.nextInt(900000);

        return prefix + randomNumber;
    }

    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest){
        Account account = accountRepository.findAccountByEmail(forgotPasswordRequest.getEmail());
        if(account == null){
            throw new NotFoundException("Email not found !");
        }else{
            EmailDetail emailDetail = new EmailDetail();
            emailDetail.setReceiver(account);
            emailDetail.setSubject("Reset Password");
            emailDetail.setLink("https://www.google.com/?token="+tokenService.generateToken(account));
            emailService.sendEmail(emailDetail);
        }
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest){
        Account account = getCurrentAccount();
        account.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        accountRepository.save(account);

    }

    public String changePassword(ChangePasswordRequest changePasswordRequest) {
        Account account = getCurrentAccount();
        String originPassword = account.getPassword();
        String message;

        try {
            if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), originPassword)) {
                message = "Invalid current password!";
                return message;
            }

            account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            accountRepository.save(account);
            message = "Successfully change your password!";

            return message;
        }catch (Exception e) {
            throw new AuthException(e.getMessage());
        }
    }

    public UserResponse loginGoogle(String token){
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String email = decodedToken.getEmail();
            Account account = accountRepository.findAccountByEmail(email);
            if(account == null) {
                Account newAccount = new Account();
                newAccount.setUserName(decodedToken.getEmail());
                newAccount.setEmail(decodedToken.getEmail());
                newAccount.setFullName(decodedToken.getName());
                newAccount.setImage(decodedToken.getPicture());
                newAccount.setRole(Role.CUSTOMER);
                account=accountRepository.save(newAccount);
            }
            UserResponse userResponse = new UserResponse();
            userResponse.setToken(token);
            userResponse.setFullName(account.getFullName());
            userResponse.setImage(account.getImage());
            userResponse.setRole(account.getRole());

            return userResponse;

        } catch (Exception e) {
            throw new RuntimeException("Google token verification failed", e);
        }

    }

}

