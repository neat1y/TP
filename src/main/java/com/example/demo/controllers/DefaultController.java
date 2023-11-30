package com.example.demo.controllers;

import com.example.demo.models.Currency;
import com.example.demo.models.Operations;
import com.example.demo.models.Person;
import com.example.demo.service.CurrencyService;
import com.example.demo.service.OperationService;
import com.example.demo.service.PersonService;
import com.example.demo.util.NumberValidator;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.Objects;

@Controller
@RequestMapping("/operations")
public class DefaultController {
    private final OperationService operationService;
    private final PersonService personService;
    private final CurrencyService currencyService;
    private final NumberValidator numberValidator;
    @Autowired
    public DefaultController(OperationService operationService, PersonService personService, CurrencyService currencyService, NumberValidator numberValidator) {
        this.operationService = operationService;
        this.personService = personService;
        this.currencyService = currencyService;
        this.numberValidator = numberValidator;
    }

    @GetMapping()
    public String hello(Model model, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Person person =personService.findbyName(userDetails.getUsername()).get();
        Operations operations=operationService.findByLast().orElse(null);
        Currency currency=currencyService.findLast();
        model.addAttribute("last_oper",operations);
        model.addAttribute("value",currency);
        model.addAttribute("person",person);
        return "auth/def/trade";
    }
    @PostMapping()
    public String trade(Operations operations, Model model, @RequestParam String choose,
                        @RequestParam String value, @Valid @RequestParam Double money, BindingResult bindingResult, @RequestParam Integer id){
        numberValidator.validate(money,bindingResult);
        if(bindingResult.hasErrors()){
            return "auth/def/trade";
        }
        Date currentDate = new Date();
        // Преобразуем LocalDateTime в Timestamp
        operations.setOperation_Date(currentDate);
        Person person=personService.findById(id).get();
        operations.setPerson(person);
        operations.setCurrency(currencyService.findLast());
        if(Objects.equals(choose, "buy")){
            operations.setBuy(money);
            operations.setSell(0);

            if(Objects.equals(value, "euro")){
                operations.setEuro(true);
            }
            else{
                operations.setDollar(true);
            }
        }
        else{
            operations.setBuy(0);
            operations.setSell(money);
            if(Objects.equals(value, "euro")){
                operations.setEuro(true);
            }
            else{
                operations.setDollar(true);
            }
        }


        operationService.save(operations);
        model.addAttribute("operations",operations);
        model.addAttribute("kurs",currencyService.findLast());
        model.addAttribute("person",personService.findById(id));
        return "auth/def/receipt";
    }
}
