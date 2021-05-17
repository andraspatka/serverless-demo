package andras.patka.function;

import java.util.Arrays;

public class CategoryNotFoundException extends IllegalArgumentException {

    public CategoryNotFoundException() {
        super("Category not found! Please provide one of the following categories: " + Arrays.toString(Categories.values()));
    }
    
}
