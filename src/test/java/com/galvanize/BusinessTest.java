package com.galvanize;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BusinessTest {

    @Test
    public void test01_addressClassShouldWork() {
        Class<?> addressClass = getAddress();
        assessAddressFields(addressClass);
        assessAddressConstructor(addressClass);
        assertAddressGettersAndSetters(addressClass);
        assertAddressWorks(addressClass);
        assertToStringWorks(addressClass);
    }

    @Test
    public void test02_addressableInterfaceShouldExist() {
        Class<?> addressable = assertAddressableInterface();
        assertAddressableGetAddresses(addressable);
        assertAddressableAddAddress(addressable);
    }

    @Test
    public void test03_businessClassShouldExist() {
        Class<?> business = assertBusinessClass();
        assertBusinessImplementsAddressable(business);
        assessBusinessConstructor(business);
        assessAddressGetterAndSetter(business);
    }

    private void assessBusinessConstructor(Class<?> businessClass) {
        Object business;
        try {
            business = businessClass
                    .getConstructor(String.class)
                    .newInstance("Acme");
        } catch (NoSuchMethodException e) {
            fail("Expected Business class to have a constructor that takes 1 String parameter (name)");
            return;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        try {
            assertConstructorParameterWorks(business, "getName", "Acme");
        } catch (NoSuchMethodException e) {
            fail("Expected Business class to have a method named getName");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void assessAddressGetterAndSetter(Class<?> businessClass) {
        Object business = null;
        try {
            business = businessClass.getConstructors()[0].newInstance("Acme");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            fail("Could not instantiate a Business object with new Business(\"Acme\");");
        }

        Class<?> addressClass;
        Object address;
        try {
            addressClass = Class.forName("com.galvanize.Address");
            address = addressClass.getConstructors()[0].newInstance("15 Main", "New York", "NY", "10012");
        } catch (ClassNotFoundException e) {
            fail("You haven't implemented the Address class yet");
            return;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            fail("You haven't implemented the Address class correctly");
            return;
        }

        try {
            Method addAddressMethod = businessClass.getMethod("addAddress", addressClass);
            addAddressMethod.invoke(business, address);

            Method getAddressesMethod = businessClass.getMethod("getAddresses");
            List<?> addresses = (List) getAddressesMethod.invoke(business);
            assertEquals("Expected addAddress / getAddresses to work but they didn't", address, addresses.get(0));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            fail("You haven't implemented the addAddress method on Business yet");
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            fail("You haven't implemented the addAddress / getAddresses methods on Business correctly");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void assertBusinessImplementsAddressable(Class<?> business) {
        Type[] interfaces = business.getGenericInterfaces();
        try {
            Class<?> addressable = Class.forName("com.galvanize.Addressable");
            for (Type actualInterface : interfaces) {
                if (actualInterface == addressable) {
                    return;
                }
            }
            fail("Expected Business to implement Addressable");
        } catch (ClassNotFoundException e) {
            fail("You haven't implemented Addressable yet");
        }
    }

    private void assessAddressConstructor(Class<?> addressClass) {
        try {
            addressClass.getConstructor(String.class, String.class, String.class, String.class);
        } catch (NoSuchMethodException e) {
            fail("Expected Address class to have a constructor that takes 4 String parameters (street, city, state and zip)");
        }
    }

    private void assessAddressFields(Class<?> addressClass) {
        Arrays.asList("street", "city", "state", "zip").forEach(fieldName -> {
            try {
                Field field = addressClass.getDeclaredField(fieldName);
                assertSame("Expected the " + fieldName + " field on Address to be a String", field.getType(), String.class);
                assertTrue("Expected the " + fieldName + " field on Address to be private", Modifier.isPrivate(field.getModifiers()));
            } catch (NoSuchFieldException e) {
                fail("Expected Address class to have a private String field named " + fieldName);
            }
        });
    }

    private void assertAddressGettersAndSetters(Class<?> addressClass) {
        Arrays.asList("getStreet", "getCity", "getState", "getZip").forEach(fieldName -> {
            try {
                Method method = addressClass.getDeclaredMethod(fieldName);
                assertSame("Expected the " + fieldName + " method on Address to return a String", method.getReturnType(), String.class);
                assertTrue("Expected the " + fieldName + " method on Address to be public", Modifier.isPublic(method.getModifiers()));
            } catch (NoSuchMethodException e) {
                fail("Expected Address class to have a public method named " + fieldName);
            }
        });

        Arrays.asList("setStreet", "setCity", "setState", "setZip").forEach(fieldName -> {
            try {
                Method method = addressClass.getDeclaredMethod(fieldName, String.class);
                assertSame("Expected the " + fieldName + " method on Address to return void", method.getReturnType(), Void.TYPE);
                assertTrue("Expected the " + fieldName + " method on Address to be public", Modifier.isPublic(method.getModifiers()));
            } catch (NoSuchMethodException e) {
                fail("Expected Address class to have a public method named " + fieldName + " that takes a String");
            }
        });
    }

    private void assertAddressWorks(Class<?> addressClass) {
        try {
            Object address = addressClass
                    .getConstructor(String.class, String.class, String.class, String.class)
                    .newInstance("Street", "City", "ST", "55555");

            assertConstructorParameterWorks(address, "getStreet", "Street");
            assertConstructorParameterWorks(address, "getCity", "City");
            assertConstructorParameterWorks(address, "getState", "ST");
            assertConstructorParameterWorks(address, "getZip", "55555");
            assertSetterWorks(address, "Street");
            assertSetterWorks(address, "City");
            assertSetterWorks(address, "State");
            assertSetterWorks(address, "Zip");
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            fail("An error has occurred.  Please see the stack trace above for more details");
        }
    }

    private void assertSetterWorks(Object address, String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method setter = address.getClass().getMethod("set" + fieldName, String.class);
        setter.invoke(address, "new value");
        String result = (String) address.getClass().getMethod("get" + fieldName).invoke(address);
        assertEquals("Expected set" + fieldName + "() to work but it didn't", "new value", result);
    }

    private void assertToStringWorks(Class<?> addressClass) {
        try {
            Object address = addressClass
                    .getConstructor(String.class, String.class, String.class, String.class)
                    .newInstance("Street", "City", "ST", "55555");

            assertEquals("You haven't implemented Address toString correctly", address.toString(), "Street, City, ST 55555");
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void assertConstructorParameterWorks(Object object, String fieldName, String value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method method = object.getClass().getMethod(fieldName);
        String result = (String) method.invoke(object);
        assertEquals("Expected " + fieldName + "() to return the value passed to the constructor", value, result);
    }

    private Class<?> getAddress() {
        try {
            return Class.forName("com.galvanize.Address");
        } catch (ClassNotFoundException e) {
            fail("You have not implemented the Address class yet (ClassNotFoundException)");
            return null;
        }
    }

    private Class<?> assertBusinessClass() {
        try {
            return Class.forName("com.galvanize.Business");
        } catch (ClassNotFoundException e) {
            fail("You have not implemented the Business class yet (ClassNotFoundException)");
            return null;
        }
    }

    private Class<?> assertAddressableInterface() {
        try {
            Class<?> addressable = Class.forName("com.galvanize.Addressable");
            assertTrue("Expected Addressable to be an interface", addressable.isInterface());
            return addressable;
        } catch (ClassNotFoundException e) {
            fail("You have not implemented the Addressable interface yet (ClassNotFoundException)");
            return null;
        }
    }

    private void assertAddressableGetAddresses(Class<?> addressable) {
        try {
            Method getAddresses = addressable.getMethod("getAddresses");
            assertEquals("Expected getAddresses to return a List<Address>", getAddresses.getReturnType(), List.class);

            ParameterizedType getAddressesReturnType = (ParameterizedType) getAddresses.getGenericReturnType();
            assertEquals(getAddressesReturnType.getActualTypeArguments()[0], Class.forName("com.galvanize.Address"));
        } catch (NoSuchMethodException e) {
            fail("Expected Addressable to declare a method named getAddresses");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void assertAddressableAddAddress(Class<?> addressable) {
        try {
            Method addAddressMethod = addressable.getMethod("addAddress", Class.forName("com.galvanize.Address"));
            assertEquals("Expected addAddress to return void", addAddressMethod.getReturnType(), Void.TYPE);
        } catch (NoSuchMethodException e) {
            fail("Expected Addressable to declare a method named addAddress, with one parameter of type Address");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
