package andras.patka.function;

public enum Categories {
    FRUITS,
    CARS,
    DOGS;

    public static Categories valueFromString(String category) throws CategoryNotFoundException {
        try {
            return Categories.valueOf(category);
        } catch (IllegalArgumentException e) {
            throw new CategoryNotFoundException();
        }
    }   
}
