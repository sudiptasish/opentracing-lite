/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.hm.otl.metrics.server;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;

/**
 * Platform MBean Server.
 *
 * @author Sudiptasish Chanda
 */
public class PlatformMBeanServer {
    
    private static final PlatformMBeanServer SERVER = new PlatformMBeanServer();
    
    public static final String OTL_DOMAIN = "com.sc.hm.otl";
    
    private final MBeanServer mbeanServer;
    
    private PlatformMBeanServer() {
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
    }
    
    /**
     * Return the singleton mbean server.
     * @return PlatformMBeanServer
     */
    public static PlatformMBeanServer get() {
        return SERVER;
    }
    
    public void register(String type, String name, Object mbean)
        throws MalformedObjectNameException
            , InstanceAlreadyExistsException
            , MBeanRegistrationException
            , NotCompliantMBeanException {

        ObjectName objectName = new ObjectName(OTL_DOMAIN + ":type=" + type + ",name=" + name);
        mbeanServer.registerMBean(mbean, objectName);
    }
    
    public void unregister(String type, String name)
        throws MalformedObjectNameException
            , InstanceAlreadyExistsException
            , MBeanRegistrationException
            , NotCompliantMBeanException
            , InstanceNotFoundException {

        ObjectName objectName = new ObjectName(OTL_DOMAIN + ":type=" + type + ",name=" + name);
        mbeanServer.unregisterMBean(objectName);
    }
    
    public String getDefaultDomain() {
        return mbeanServer.getDefaultDomain();
    }
    
    public int mbeanCount() {
        return mbeanServer.getMBeanCount();
    }
    
    public MBeanInfo getMBeanInfo(ObjectName name)
            throws InstanceNotFoundException, IntrospectionException,
                   ReflectionException {
        
        return mbeanServer.getMBeanInfo(name);
    }
    
    public void setAttribute(ObjectName name, Attribute attribute)
            throws InstanceNotFoundException, AttributeNotFoundException,
                   InvalidAttributeValueException, MBeanException,
                   ReflectionException, IOException {
        
        mbeanServer.setAttribute(name, attribute);
    }
    
    public AttributeList setAttributes(ObjectName name, AttributeList attributes)
        throws InstanceNotFoundException, ReflectionException, IOException {
        
        return mbeanServer.setAttributes(name, attributes);
    }
    
    public AttributeList getAttributes(ObjectName name, String[] attributes)
            throws InstanceNotFoundException, ReflectionException,
                   IOException {
        
        return mbeanServer.getAttributes(name, attributes);
    }
    
    public Object getAttribute(ObjectName name, String attribute)
            throws MBeanException, AttributeNotFoundException,
                   InstanceNotFoundException, ReflectionException,
                   IOException {
        
        return mbeanServer.getAttribute(name, attribute);
    }
    
    public Object invoke(ObjectName name, String operationName,
                         Object params[], String signature[])
            throws InstanceNotFoundException, MBeanException,
                   ReflectionException, IOException {
        
        return mbeanServer.invoke(name, operationName, params, signature);
    }
    
    public Set<ObjectName> queryNames(ObjectName name, QueryExp query) {
        return mbeanServer.queryNames(name, query);
    }
    
    public Set<ObjectInstance> queryMBeans(ObjectName name, QueryExp query) {
        return mbeanServer.queryMBeans(name, query);
    }
}
