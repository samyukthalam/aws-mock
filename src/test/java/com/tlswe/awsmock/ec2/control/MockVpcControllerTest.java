package com.tlswe.awsmock.ec2.control;


import com.tlswe.awsmock.ec2.model.MockVpc;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MockVpcController.class})
@PowerMockIgnore({ "javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*",
        "org.xml.*", "org.w3c.dom.*", "com.sun.org.apache.xalan.*", "javax.activation.*" })
public class MockVpcControllerTest {

    @Test
    public void Test_getInstance() {
        MockVpcController instance = MockVpcController.getInstance();
        Assert.assertNotNull(instance);
    }

    /*
     * This may be pointing to a bug in the code. Although the instances are not there, the describeVpcs returns
     * NULLs in the collection with equal size of input. Therefore if we have two invalid instances, it would return two
     * records of NULL.
     */
    @Test
    public void Test_describeVpc() throws Exception {
        
        MockVpcController controller = Mockito.spy(MockVpcController.class);

        Map<String, MockVpc> allMockVpc = new ConcurrentHashMap<String, MockVpc>();
        MockVpc mockVpc = new MockVpc();
        allMockVpc.put("i-2323", mockVpc);
        MockVpc mockVpc1 = new MockVpc();
        allMockVpc.put("i-23223233", mockVpc1);

        MemberModifier.field(MockVpcController.class, "allMockVpcInstances").set(controller,
                allMockVpc);

        Collection<MockVpc> collectionOfMockVpc = controller.describeVpcs();

        int collectionCount = collectionOfMockVpc.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
    }
    
    /*
     * This may be pointing to a bug in the code. Although the instances are not there, the describeVpcs returns
     * NULLs in the collection with equal size of input. Therefore if we have two invalid instances, it would return two
     * records of NULL.
     */
    @Test
    public void Test_restoreAllVpc() throws Exception {
        
        MockVpcController controller = Mockito.spy(MockVpcController.class);

        Map<String, MockVpc> allMockVpc = new ConcurrentHashMap<String, MockVpc>();
        MockVpc mockVpc = new MockVpc();
        mockVpc.setVpcId("vpc-asdasd");
        allMockVpc.put("i-2323", mockVpc);
        MockVpc mockVpc1 = new MockVpc();
        mockVpc1.setVpcId("vpc-awewewewe");
        allMockVpc.put("i-23223233", mockVpc1);

        MemberModifier.field(MockVpcController.class, "allMockVpcInstances").set(controller,
                allMockVpc);

        Collection<MockVpc> collectionOfMockVpc = controller.describeVpcs();
        Collection<MockVpc> restoreVpcs = new ArrayList<MockVpc>(collectionOfMockVpc.size());
        for (MockVpc mockVpcRes : collectionOfMockVpc)
        {
        	restoreVpcs.add(mockVpcRes);
        }
        int collectionCount = collectionOfMockVpc.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
        
        for(MockVpc restoreVpc : collectionOfMockVpc) {
        	controller.deleteVpc(restoreVpc.getVpcId());
        }
        
        controller.restoreAllMockVpc(restoreVpcs);
        
        collectionOfMockVpc = controller.describeVpcs();

        collectionCount = collectionOfMockVpc.size();

        // Returns collection of size 2
        Assert.assertEquals(2, collectionCount);
    }
    
    @Test
    public void Test_createVpc() throws Exception {
        
       MockVpc mockVpc = MockVpcController
                .getInstance()
                .createVpc("TestCDR", "Deafult");

    
        Assert.assertNotNull("Internet gateway created.", mockVpc.getVpcId());
    }
    
    @Test
    public void Test_deleteVpc() throws Exception {
        
       MockVpc mockVpc = MockVpcController
                .getInstance()
                .createVpc("TestCDR", "Deafult");
       MockVpc mockVpcDelete = MockVpcController
               .getInstance()
               .deleteVpc(mockVpc.getVpcId());
    
        Assert.assertNotNull("Internet gateway deleted.", mockVpcDelete.getVpcId());
    }

}