/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package org.zaproxy.zest.test.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.zaproxy.zest.core.v1.ZestExpressionURL;
import org.zaproxy.zest.core.v1.ZestRequest;
import org.zaproxy.zest.core.v1.ZestResponse;

/** */
class ZestExpressionURLUnitTest {

    List<String> includeStrings = new LinkedList<>();
    List<String> excludeStrings = new LinkedList<>();
    int includeSize = 10;
    int excludeSize = 5;

    {
        for (int i = 0; i < includeSize; i++) includeStrings.add("PING" + i);
        for (int i = 0; i < excludeSize; i++) excludeStrings.add("PONG" + i);
    }

    @Test
    void testZestExpressionURLListOfStringListOfString() {
        ZestExpressionURL urlExpr = new ZestExpressionURL(includeStrings, excludeStrings);
        for (int i = 0; i < includeSize; i++) {
            String obtained = urlExpr.getIncludeRegexes().get(i);
            String expected = "PING" + i;
            String msg = "INCLUDE Expected " + expected + " instead of" + obtained;
            assertEquals(expected, obtained);
        }
        for (int i = 0; i < excludeSize; i++) {
            String obtained = urlExpr.getExcludeRegexes().get(i);
            String expected = "PONG" + i;
            String msg = "EXCLUDE Expected " + expected + " instead of" + obtained;
            assertEquals(expected, obtained);
        }
    }

    @Test
    void testIsTrue() {
        ZestExpressionURL urlExpr = new ZestExpressionURL();
        urlExpr.setIncludeRegexes(includeStrings);
        urlExpr.setExcludeRegexes(excludeStrings);
        try {
            ZestRequest request = new ZestRequest();
            request.setUrl(new URL("http://www.PING1.com"));
            assertTrue(urlExpr.isTrue(new TestRuntime(request)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testIsTrueFalse() {
        ZestExpressionURL urlExpr = new ZestExpressionURL();
        urlExpr.setIncludeRegexes(includeStrings);
        urlExpr.setExcludeRegexes(excludeStrings);
        try {
            ZestRequest request = new ZestRequest();
            request.setUrl(new URL("http://www.PONG1.com"));
            assertFalse(urlExpr.isTrue(new TestRuntime(request)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testIsTrueDifferentURL() {
        ZestExpressionURL urlExpr = new ZestExpressionURL();
        urlExpr.setIncludeRegexes(includeStrings);
        urlExpr.setExcludeRegexes(excludeStrings);
        try {
            ZestRequest request = new ZestRequest();
            request.setUrl(new URL("http://www.asdf.com"));
            assertFalse(urlExpr.isTrue(new TestRuntime(request)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetIncludeRegexes() {
        ZestExpressionURL urlExpr = new ZestExpressionURL();
        urlExpr.setExcludeRegexes(excludeStrings);
        urlExpr.setIncludeRegexes(includeStrings);
        List<String> includeRegex = urlExpr.getIncludeRegexes();
        for (int i = 0; i < includeSize; i++) {
            String obtained = includeRegex.get(i);
            String expected = includeStrings.get(i);
            String msg = "INCLUDE: expected " + expected + " instead of " + obtained;
            assertEquals(expected, obtained);
        }
    }

    @Test
    void testGetExcludeRegexes() {
        ZestExpressionURL urlExpr = new ZestExpressionURL();
        urlExpr.setExcludeRegexes(excludeStrings);
        urlExpr.setIncludeRegexes(includeStrings);
        List<String> excludeRegex = urlExpr.getExcludeRegexes();
        for (int i = 0; i < excludeSize; i++) {
            String obtained = excludeRegex.get(i);
            String expected = excludeStrings.get(i);
            String msg = "EXCLUDE: expected " + expected + " instead of " + obtained;
            assertEquals(expected, obtained);
        }
    }

    @Test
    void testDeepCopy() {
        ZestExpressionURL urlExpr = new ZestExpressionURL(includeStrings, excludeStrings);
        urlExpr.setInverse(true);
        ZestExpressionURL copy = urlExpr.deepCopy();
        assertEquals(copy.isInverse(), urlExpr.isInverse());
        for (int i = 0; i < includeSize; i++) {
            String obtained = copy.getIncludeRegexes().get(i);
            String expected = includeStrings.get(i);
            String msg = "INCLUDE: expected " + expected + " instead of " + obtained;
            assertEquals(expected, obtained);
        }
        for (int i = 0; i < excludeSize; i++) {
            String obtained = copy.getExcludeRegexes().get(i);
            String expected = excludeStrings.get(i);
            String msg = "EXCLUDE: expected " + expected + " instead of " + obtained;
            assertEquals(expected, obtained);
        }
    }

    @Test
    void testDeepCopyNoPointer() {
        ZestExpressionURL urlExpr = new ZestExpressionURL(includeStrings, excludeStrings);
        ZestExpressionURL copy = urlExpr.deepCopy();
        urlExpr.setExcludeRegexes(null);
        urlExpr.setIncludeRegexes(null);
        for (int i = 0; i < includeSize; i++) {
            String obtained = copy.getIncludeRegexes().get(i);
            String msg = "INCLUDE: " + i;
            assertEquals(includeStrings.get(i), obtained);
        }
        for (int i = 0; i < excludeSize; i++) {
            String obtained = copy.getExcludeRegexes().get(i);
            String msg = "EXCLUDE: " + i;
            assertEquals(excludeStrings.get(i), obtained);
        }
    }

    @Test
    void testIsTrueExcludePattern() {
        try {
            ZestResponse response =
                    new ZestResponse(new URL("http://www.PONG19874.com"), "", "", 200, 100);
            ZestExpressionURL urlExpr = new ZestExpressionURL(includeStrings, excludeStrings);
            assertFalse(urlExpr.isTrue(new TestRuntime(response)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
