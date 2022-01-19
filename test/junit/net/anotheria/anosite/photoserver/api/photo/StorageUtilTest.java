package net.anotheria.anosite.photoserver.api.photo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
/**
 * @author ykalapusha
 */
@RunWith(MockitoJUnitRunner.class)
public class StorageUtilTest {

    @Test
    public void testId() {
        long id = 1111L;
        String extension = ".tmp";

        long expected = StorageUtil.getId(id + extension);
        assertEquals(expected, id);

        long expected2 = StorageUtil.getId(extension);
        assertEquals(expected2, 0);

        long expected3 = StorageUtil.getId(123456 + "");
        assertEquals(expected3, 123456L);

        long expected4 = StorageUtil.getId(null);
        assertEquals(expected4, 0);

        long expected5 = StorageUtil.getId("");
        assertEquals(expected5, 0);

        long expected6 = StorageUtil.getId(id + ".");
        assertEquals(expected6, id);
    }

    @Test
    public void testExtension() {
        long id = 1111L;
        String extension = ".tmp";

        String expected = StorageUtil.getExtension(id + extension);
        assertEquals(expected, extension);

        String expected2 = StorageUtil.getExtension(id + "");
        assertEquals(expected2, "");

        String expected3 = StorageUtil.getExtension(id + ".");
        assertEquals(expected3, ".");

        String expected4 = StorageUtil.getExtension(extension);
        assertEquals(expected4, extension);

        String expected5 = StorageUtil.getExtension(id + ". ");
        assertEquals(expected5, ". ");
    }
}
