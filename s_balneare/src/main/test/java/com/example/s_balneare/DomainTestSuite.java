package com.example.s_balneare;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Domain Logic Only")
@SelectPackages("com.example.s_balneare.domain")
public class DomainTestSuite { }