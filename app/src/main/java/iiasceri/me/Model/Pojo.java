package iiasceri.me.Model;

public class Pojo {

    @Override
    public String toString() {
        return "Pojo{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", category='" + category + '\'' +
                ", imageName='" + imageName + '\'' +
                '}';
    }

    /**
     * Ugly POJO TODO: Refactor all under app (price => schedule etc)
     */
    private final String name;
    private final String description;
    private final String price;
    private final String category;
    private final String imageName;

    public Pojo(String name, String description, String price, String category,
                String imageName) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImageName() {
        return imageName;
    }
}
