package com.wrmanager.wrmanagerfx.requests;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;



class PipelineRequestsTest {

@Test
    void testProduct(){

        PipelineRequests.getProductFromImage("./vig.jpg");
    }


}