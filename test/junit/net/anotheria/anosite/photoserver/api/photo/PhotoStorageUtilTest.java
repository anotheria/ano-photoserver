package net.anotheria.anosite.photoserver.api.photo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
/**
 * @author ykalapusha
 */
@RunWith(MockitoJUnitRunner.class)
public class PhotoStorageUtilTest {

    @Test
    public void testId() {
        long id = 1111L;
        String actualId = String.valueOf(id);
        String extension = ".tmp";

        String expected = PhotoStorageUtil.getId(id + extension);
        assertEquals(expected, actualId);

        String expected2 = PhotoStorageUtil.getId(extension);
        assertEquals(expected2, "");

        String expected3 = PhotoStorageUtil.getId(123456 + "");
        assertEquals(expected3, String.valueOf(123456L));

        String expected4 = PhotoStorageUtil.getId(null);
        assertEquals(expected4, "");

        String expected5 = PhotoStorageUtil.getId("");
        assertEquals(expected5, "");

        String expected6 = PhotoStorageUtil.getId(id + ".");
        assertEquals(expected6, actualId);

        String expected7 = PhotoStorageUtil.getId("123456_c_t4_s400.JPEG");
        assertEquals(expected7, "123456_c_t4_s400");

        String expected8 = PhotoStorageUtil.getId("123456._c_t4_s400.JPEG");
        assertEquals(expected8, "123456._c_t4_s400");
    }

    @Test
    public void testExtension() {
        long id = 1111L;
        String extension = ".tmp";

        String expected = PhotoStorageUtil.getExtension(id + extension);
        assertEquals(expected, extension);

        String expected2 = PhotoStorageUtil.getExtension(id + "");
        assertEquals(expected2, "");

        String expected3 = PhotoStorageUtil.getExtension(id + ".");
        assertEquals(expected3, ".");

        String expected4 = PhotoStorageUtil.getExtension(extension);
        assertEquals(expected4, extension);

        String expected5 = PhotoStorageUtil.getExtension(id + ". ");
        assertEquals(expected5, ". ");
    }

    @Test
    public void testOriginalId() {
        long id = 1111L;
        String actualId = String.valueOf(id);
        String extension = ".tmp";

        long expected = PhotoStorageUtil.getOriginalId(id + extension);
        assertEquals(expected, id);

        long expected2 = PhotoStorageUtil.getOriginalId(extension);
        assertEquals(expected2, -1);

        long expected3 = PhotoStorageUtil.getOriginalId(123456 + "");
        assertEquals(expected3, 123456L);

        long expected4 = PhotoStorageUtil.getOriginalId(null);
        assertEquals(expected4, -1);

        long expected5 = PhotoStorageUtil.getOriginalId("");
        assertEquals(expected5, -1);

        long expected6 = PhotoStorageUtil.getOriginalId(id + ".");
        assertEquals(expected6, id);

        long expected7 = PhotoStorageUtil.getOriginalId("123456_c_t4_s400.JPEG");
        assertEquals(expected7, 123456L);
    }
}
