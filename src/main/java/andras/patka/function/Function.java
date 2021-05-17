package andras.patka.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.List;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    public static final String storageConnectionString =
        "";


    ItemService itemService;

    public Function() {
        itemService = new ItemService(storageConnectionString);
    }

    public Function(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * curl "{your host}/api/HttpExample?name=HTTP%20Query&category=fruits"
     */
    @FunctionName("items")
    public HttpResponseMessage addItem(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        // Parse query parameter
        final String queryName = request.getQueryParameters().get("name");
        final String queryCategory = request.getQueryParameters().get("category");
        Categories category;

        context.getLogger().info("Add item with parameters: name: " + queryName + " category: " + queryCategory);

        if (queryName == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string").build();
        }
        if (queryCategory == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a category on the query string").build();
        }
        try {
            category = Categories.valueFromString(queryCategory.toUpperCase());
        } catch(CategoryNotFoundException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(e.getMessage()).build();
        }
        itemService.saveToTable(queryName.toUpperCase(), category);
        return request.createResponseBuilder(HttpStatus.OK).body("Saved to table: " + queryName.toUpperCase() + " category: " + queryCategory.toUpperCase()).build();
    }

    @FunctionName("items")
    public HttpResponseMessage getItems(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        // Parse query parameter
        final String queryCategory = request.getQueryParameters().get("category");
        Categories category;

        context.getLogger().info("Get items with parameters: category: " + queryCategory);

        if (queryCategory == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a category on the query string").build();
        }
        try {
            category = Categories.valueFromString(queryCategory.toUpperCase());
        } catch(CategoryNotFoundException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(e.getMessage()).build();
        }

        List<String> items = itemService.getItemsOfCategory(category);
        return request.createResponseBuilder(HttpStatus.OK).body("Items of category: " + category.toString() + ": " + items).build();
    }
    
}
