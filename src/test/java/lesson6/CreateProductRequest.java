package lesson6;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CreateProductRequest {
    private Integer id;
    private String title;
    private Integer price;
    private String categoryTitle;

}
