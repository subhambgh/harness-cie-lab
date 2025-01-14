package com.nikp.payment.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import com.nikp.payment.api.UserService;
import com.nikp.payment.domain.PaymentAndUser;
import com.nikp.payment.domain.User;
import com.nikp.payment.domain.UserDto;
import com.nikp.payment.infrastructure.exceptions.UserNotFoundException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/users")
public class UserController {
  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

  private final UserService userService;

  @Value("${harness.user4}")
  private String user4;

  @PostConstruct
  public void insertUsers() {
    userService.insert(new User("T1", "m@m.pl"));
    userService.insert(new User("T2", "m2@m.pl"));
    userService.insert(new User("T3", "m3@m.pl"));
    userService.insert(new User(user4, "m3@m.pl"));
  }

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }


  @GetMapping(value = "", produces = "application/json")
  public List<UserDto> getAllUsers() {
    LOG.info("Fetching all the users");
    return userService.getAllUsers().stream().map(
        u -> new UserDto(u.getId(), u.getName(), u.getEmail())
    ).collect(Collectors.toList());
  }

  @GetMapping(value = "payments-for-user/{userId}", produces = "application/json")
  public PaymentAndUser paymentAndUsers(@PathVariable final String userId) throws UserNotFoundException {
    Optional<PaymentAndUser> paymentAndUsersForUserId = userService.getPaymentAndUsersForUserId(userId);
    if (!paymentAndUsersForUserId.isPresent()) {
      throw new UserNotFoundException("Payments for user id: " + userId + " not found");
    } else {
      return paymentAndUsersForUserId.get();
    }

  }


}
