package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.core.utilities.ldap.LdapService;
import com.skylab.superapp.dataAccess.LdapGroupDao;
import com.skylab.superapp.entities.LdapGroup;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final LdapService ldapService;


    public GroupController(LdapService ldapService) {
        this.ldapService = ldapService;
    }


    @PostMapping("/")
    public ResponseEntity<Result> createGroup(@RequestBody CreateGroupRequest request) {
        ldapService.createGroup(request.getGroupName());

       return ResponseEntity.status(HttpStatus.CREATED).body(
               new SuccessResult(
                       "Group created successfully",
                       HttpStatus.CREATED
               )
       );

    }

}
