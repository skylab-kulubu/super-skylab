package com.skylab.superapp.core.utilities.ldap;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchControls;

@Service
public class EmployeeNumberGenerator {

    private final LdapTemplate ldapTemplate;

    public EmployeeNumberGenerator(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public synchronized String generateNext(){
        long count = countUsers();

        long nextNumber = count+1;

        return formatEmployeeNumber(nextNumber);
    }



    private long countUsers() {
        try{
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            controls.setCountLimit(0);

            return ldapTemplate.search(
                    "ou=people",
                    "(objectClass=inetOrgPerson)",
                    controls,
                    (Object ctx) -> ctx
            ).size();

        }catch (Exception e){
            return 0;
        }
    }


    private String formatEmployeeNumber(long number) {
        if (number <= 0){
            throw new IllegalArgumentException("Employee number must be greater than 0");
        }

        if (number > 9999999) {
            throw new IllegalStateException(
                    "Employee number limit reached! Maximum: 9,999,999"
            );
        }

        return String.format("SKY-%07d", number);

    }

    public Long parseEmployeeNumber(String employeeNumber){
        if (employeeNumber == null || !employeeNumber.startsWith("SKY-")){
            throw new IllegalStateException("Invalid employee number format");
        }

        try {
            String numberPart = employeeNumber.substring(4);
            return Long.parseLong(numberPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid employee number: " + employeeNumber);
        }

    }

    public boolean isValid(String employeeNumber) {
        if (employeeNumber == null) return false;
        return employeeNumber.matches("^SKY-\\d{7}$");
    }
}
