package homework15;

/**
 * Created by Norman on 2017.07.02..
 */

public class QueryConstants {
    /**
     * Stored procedures.
     */
    public static final String SELECT_MAX_USER_ID_QUERY = "SELECT max(u.user_id) FROM Users u";
    public static final String SELECT_ALL_USER_QUERY = "SELECT u FROM Users u";
    public static final String SELECT_MAX_PRODUCT_ID_QUERY = "SELECT max(s.id) FROM Storage s";
    public static final String SELECT_ALL_PRODUCT_QUERY = "SELECT s FROM Storage s";
}
