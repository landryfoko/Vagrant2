package test.com.libertas.vipaas;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.json.simple.JSONObject;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.libertas.vipaas.services.hls.HLSProviderController;
import com.libertas.vipaas.services.hls.HLSProviderService;

public class HLSControllerTest extends AbstractRestControllerTest {
    @Test
    public void test() throws Exception {
        final HLSProviderService hlsProviderService = mock(HLSProviderService.class);

        final JSONObject expectedResult = new JSONObject();

        expectedResult.put("key", "123");

        final JSONObject request = new JSONObject();

        request.put("test", "test");

        when(hlsProviderService.getHLSCredentials(request)).thenReturn(expectedResult);

        final HLSProviderController hlsProviderController = new HLSProviderController();

        ReflectionTestUtils.setField(hlsProviderController, "hlsProviderService", hlsProviderService);

        final JSONObject result = hlsProviderController.getHLSCredentials(request);

        assertEquals(expectedResult, result);
    }
}
