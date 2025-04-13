package wordhunt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import wordhunt.utils.MimeConst;
import wordhunt.utils.MimeUtils;

class MimeUtilsTest {

    @Test
    void isTextTypeForNull() {
        assertFalse(MimeUtils.isTextType(null));
    }

    @Test
    void isTextTypeForEmpty() {
        assertFalse(MimeUtils.isTextType(""));
    }

    @Test
    void isTextTypeForTextType() {
        assertTrue(MimeUtils.isTextType(MimeConst.PLAIN_TEXT));
    }

    @Test
    void isTextTypeForJson() {
        assertTrue(MimeUtils.isTextType(MimeConst.JSON_TYPE));
    }

    @Test
    void isTextTypeForNonText() {
        assertFalse(MimeUtils.isTextType(MimeConst.PDF_TYPE));
    }
}