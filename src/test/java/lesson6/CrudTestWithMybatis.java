package lesson6;

package lesson6;

import com.geekbrains.db.dao.ProductsMapper;
import com.geekbrains.db.model.Products;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static io.restassured.RestAssured.given;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class crudTestWithMybatis {

    private static Integer idForRemove;

    @BeforeAll
    static void beforeAll() {
        RestAssured.baseURI = "https://minimarket1.herokuapp.com/market";
    }

    @Test
    @Order(1)
    void createProduct() {
        CreateProductRequest request = CreateProductRequest.builder()
                .title("Cucumber")
                .price(15)
                .categoryTitle("Food")
                .build();

        String actually = given()
                .contentType(ContentType.JSON)
                .body(request)
                .expect()
                .log()
                .body()
                .statusCode(201)
                .when()
                .post("/api/v1/products")
                .asPrettyString();

        idForRemove = JsonPath.given(actually).get("id");

    }

    @Test
    @Order(2)
    void checkAndDeleteCreatedProduct() throws IOException {
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder()
                .build(Resources.getResourceAsStream("myBatisConfig.xml"));

        try (SqlSession session = sessionFactory.openSession()) {
            ProductsMapper productsMapper = session.getMapper(ProductsMapper.class);
            Products product = productsMapper.selectByPrimaryKey(Long.valueOf(idForRemove));
            session.commit();
            System.out.println(product);
            productsMapper.deleteByPrimaryKey(Long.valueOf(idForRemove));
            session.commit();
        }
    }


    @Test
    @Order(3)
    void afterDeleteAssert() {
        given()
                .contentType(ContentType.JSON)
                .pathParam("id", idForRemove)
                .expect()
                .log()
                .body()
                .statusCode(404)
                .when()
                .get("/api/v1/products/{id}");
    }
}