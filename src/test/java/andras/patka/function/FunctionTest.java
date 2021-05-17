package andras.patka.function;

import com.microsoft.azure.functions.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


/**
 * Unit test for Function class.
 */
public class FunctionTest {

    private static ItemService itemService = mock(ItemService.class);

    @BeforeAll
    private static void setup() {
        doNothing().when(itemService).saveToTable(isA(String.class), isA(Categories.class));
        doReturn(List.of("APPLE", "ORANGE", "BANANA")).when(itemService).getItemsOfCategory(isA(Categories.class));
    }

    /**
     * Unit test for HttpTriggerJava method.
     */
    @Test
    public void testAddItemOk() throws Exception {
        // Setup
        final HttpRequestMessage<Optional<String>> req = mockAddItemResponse("Apple", "Fruits");
        final ExecutionContext context = mockContext();

        // Invoke
        final HttpResponseMessage ret = new Function(itemService).addItem(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals(ret.getBody(), "Saved to table: APPLE category: FRUITS");
    }

    @Test
    public void testAddItemCategoryInvalid() throws Exception {
        // Setup
        final HttpRequestMessage<Optional<String>> req = mockAddItemResponse("Guitar", "Instruments");
        final ExecutionContext context = mockContext();

        // Invoke
        final HttpResponseMessage ret = new Function(itemService).addItem(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.BAD_REQUEST);
        assertEquals(ret.getBody(), "Category not found! Please provide one of the following categories: [FRUITS, CARS, DOGS]");
    }

    @Test
    public void testGetItemsCategoryOk() throws Exception {
        // Setup
        final HttpRequestMessage<Optional<String>> req = mockGetItemsResponse("Fruits");
        final ExecutionContext context = mockContext();

        // Invoke
        final HttpResponseMessage ret = new Function(itemService).getItems(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        assertEquals(ret.getBody(), "Items of category: FRUITS: [APPLE, ORANGE, BANANA]");
    }

    @Test
    public void testGetItemsCategoryInvalid() throws Exception {
        // Setup
        final HttpRequestMessage<Optional<String>> req = mockGetItemsResponse("Instruments");
        final ExecutionContext context = mockContext();

        // Invoke
        final HttpResponseMessage ret = new Function(itemService).getItems(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.BAD_REQUEST);
        assertEquals(ret.getBody(), "Category not found! Please provide one of the following categories: [FRUITS, CARS, DOGS]");
    }

    @Test
    public void testGetItemsCategoryNotGiven() throws Exception {
        // Setup
        final HttpRequestMessage<Optional<String>> req = mockGetItemsResponse(null);
        final ExecutionContext context = mockContext();

        // Invoke
        final HttpResponseMessage ret = new Function(itemService).getItems(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.BAD_REQUEST);
        assertEquals(ret.getBody(), "Please pass a category on the query string");
    }

    private HttpRequestMessage<Optional<String>> mockAddItemResponse(String name, String category) {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("name", name);
        queryParams.put("category", category);
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        return req;
    }

    private HttpRequestMessage<Optional<String>> mockGetItemsResponse(String category) {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final Map<String, String> queryParams = new HashMap<>();
        queryParams.put("category", category);
        doReturn(queryParams).when(req).getQueryParameters();

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        doAnswer(new Answer<HttpResponseMessage.Builder>() {
            @Override
            public HttpResponseMessage.Builder answer(InvocationOnMock invocation) {
                HttpStatus status = (HttpStatus) invocation.getArguments()[0];
                return new HttpResponseMessageMock.HttpResponseMessageBuilderMock().status(status);
            }
        }).when(req).createResponseBuilder(any(HttpStatus.class));

        return req;
    }

    private ExecutionContext mockContext() {
        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();
        return context;
    }
}
