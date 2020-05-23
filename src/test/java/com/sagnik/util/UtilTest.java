package com.sagnik.util;

import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

public class UtilTest {
    class A {
        String s;

        public A() {
        }

        public A(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return "A{" +
                    "s='" + s + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            A a = (A) o;
            return Objects.equals(s, a.s);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s);
        }
    }

    class B {
        String s1;
        Integer i1;

        public B() {
        }

        public B(String s1, Integer i1) {
            this.s1 = s1;
            this.i1 = i1;
        }

        @Override
        public String toString() {
            return "B{" +
                    "s1='" + s1 + '\'' +
                    ", i1=" + i1 +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            B b = (B) o;
            return Objects.equals(s1, b.s1) &&
                    Objects.equals(i1, b.i1);
        }

        @Override
        public int hashCode() {
            return Objects.hash(s1, i1);
        }
    }

    @Test
    public void testSerialization() {
        A a = new A();
        a.s = "Hi";
        String ser = Util.serialize(a);
        assertNotNull(ser);
        assertTrue("{\"s\":\"Hi\"}".equals(ser));
    }

    @Test
    public void testDeserialization() {
        A a = Util.deSerialize("{\"s\":\"Hi\"}", A.class);
        assertNotNull(a);
        assertEquals("Hi", a.s);
    }

    @Test
    public void testComplexSerialization() {
        Map<B, A> m = Map.of(new B("s1", 1), new A("s"));
        String ser = Util.serialize(m);
        Map<B, A> m2 = Util.deSerialize(ser, (new TypeToken<Map<B, A>>() {}).getType());
        assertEquals(m, m2);
    }
}