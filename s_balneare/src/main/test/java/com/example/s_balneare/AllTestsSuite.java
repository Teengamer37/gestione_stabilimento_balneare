package com.example.s_balneare;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Full Domain and Application Test Suite")
@SelectPackages({
        "com.example.s_balneare.domain",
        "com.example.s_balneare.application.service"
})
public class AllTestsSuite {}