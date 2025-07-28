package org.example.utils;

import com.google.gson.*;
import javafx.scene.control.Alert;
import org.example.models.Transaction;
import org.example.models.TransactionCategory;
import org.example.models.User;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlUtil {

    public static User getUserByEmail(String userEmail){
        HttpURLConnection conn = null;
        try{
            conn = ApiUtil.fetchApi(
                    "/api/v1/user?email=" + userEmail,
                    ApiUtil.RequestMethod.GET, null
            );

            if(conn.getResponseCode() != 200){
                System.out.println("Error(getUserByEmail): " + conn.getResponseCode());
                return null;
            }

            String userDataJson = ApiUtil.readApiResponse(conn);
            JsonObject jsonObject = JsonParser.parseString(userDataJson).getAsJsonObject();

            int id = jsonObject.get("id").getAsInt();
            String name = jsonObject.get("name").getAsString();
            String email = jsonObject.get("email").getAsString();
            String password = jsonObject.get("password").getAsString();
            LocalDateTime createdAt = new Gson().fromJson(jsonObject.get("created_at"), LocalDateTime.class);

            return new User(id, name, email, password, createdAt);
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(conn!=null)
                conn.disconnect();
        }
        return null;
    }

    public static List<TransactionCategory> getAllTransactionCategoriesByUser(User user){
        List<TransactionCategory> categories = new ArrayList<>();
        HttpURLConnection conn = null;
        try{
            conn = ApiUtil.fetchApi(
                    "/api/v1/transaction-category/user/" + user.getId(),
                    ApiUtil.RequestMethod.GET, null
            );
            int code = conn.getResponseCode();
            if (code != 200) {
               System.out.println("Error(getAllTransactionCategoriesByUser): "+ conn.getResponseCode());
            }

            String result = ApiUtil.readApiResponse(conn);
            JsonArray resultJsonArray = new JsonParser().parse(result).getAsJsonArray();

            for(JsonElement jsonElement : resultJsonArray){
                JsonObject obj = jsonElement.getAsJsonObject();
                if (!obj.has("id") || !obj.has("categoryName") || !obj.has("categoryColor")) {
                    System.out.println("Skipping invalid item: " + obj);
                    continue;
                }
                int categoryId = obj.get("id").getAsInt();
                String categoryName = obj.get("categoryName").getAsString();
                String categoryColor = obj.get("categoryColor").getAsString();

                categories.add(new TransactionCategory(categoryId, categoryName, categoryColor));
            }
            return categories;
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(conn!=null)
                conn.disconnect();
        }
        return null;
    }

    public static List<Transaction>  getRecentTransactionByUserId(int userId, int startPage, int endPage, int size){
        List<Transaction> recentTransactions = new ArrayList<>();

        HttpURLConnection conn = null;
        try{
            conn = ApiUtil.fetchApi(
                    "/api/v1/transaction/recent/user/" +userId +"?startPage=" + startPage + "&endPage=" + endPage + "&size=" + size,
                    ApiUtil.RequestMethod.GET,
                    null
            );
            if(conn.getResponseCode() != 200){
                return null;
            }

            String results = ApiUtil.readApiResponse(conn);
            JsonArray resultJsonArray = new JsonParser().parse(results).getAsJsonArray();
            for(int i=0; i<resultJsonArray.size(); i++){
                JsonObject transactionJsonObj = resultJsonArray.get(i).getAsJsonObject();
                int transactionId = transactionJsonObj.get("id").getAsInt();

                TransactionCategory transactionCategory = null;
                if(transactionJsonObj.has("transactionCategory") && !transactionJsonObj.get("transactionCategory").isJsonNull()){
                    JsonObject transactionCategoryJsonObj = transactionJsonObj.get("transactionCategory").getAsJsonObject();
                    int transactionCategoryId = transactionCategoryJsonObj.get("id").getAsInt();
                    String transactionCategoryName = transactionCategoryJsonObj.get("categoryName").getAsString();
                    String transactionCategoryColor = transactionCategoryJsonObj.get("categoryColor").getAsString();

                    transactionCategory = new TransactionCategory(
                            transactionCategoryId,
                            transactionCategoryName,
                            transactionCategoryColor
                    );
                }

                String transactionName = transactionJsonObj.get("transactionName").getAsString();
                double transactionAmount = transactionJsonObj.get("transactionAmount").getAsDouble();
                LocalDate transactionDate = LocalDate.parse(transactionJsonObj.get("transactionDate").getAsString());
                String transactionType = transactionJsonObj.get("transactionType").getAsString();

                Transaction transaction = new Transaction(
                        transactionId,
                        transactionCategory,
                        transactionName,
                        transactionAmount,
                        transactionDate,
                        transactionType
                );

                recentTransactions.add(transaction);
            }
            return recentTransactions;
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }
        return null;
    }

    public static boolean postLoginUser(String email, String password){
        HttpURLConnection conn = null;
        try{
            conn = ApiUtil.fetchApi(
                    "/api/v1/user/login?email=" + email + "&password=" + password,
                    ApiUtil.RequestMethod.POST, null
            );
            int code = conn.getResponseCode();
            if (code != 200 && code != 201) {
                return false;
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(conn!=null)
                conn.disconnect();
        }
        return true;
    }
    public static boolean postCreateUser(JsonObject userData){
        HttpURLConnection conn = null;
        try{
            conn = ApiUtil.fetchApi(
                    "/api/v1/user",
                    ApiUtil.RequestMethod.POST,
                    userData
            );

            int code = conn.getResponseCode();
            System.out.println("Response Code: " + code);
            if (code != 200 && code != 201) {
                return false;
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(conn!=null)
                conn.disconnect();
        }
        return true;
    }

    public static boolean postTransactionCategory(JsonObject transactionCategoryData){
        HttpURLConnection conn = null;
        try{
            conn = ApiUtil.fetchApi(
                    "/api/v1/transaction-category",
                    ApiUtil.RequestMethod.POST,
                    transactionCategoryData
            );

            if(conn.getResponseCode()!=200){
                return false;
            }

            return true;
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                conn.disconnect();
            }
        }
        return false;
    }

    public static boolean postTransaction(JsonObject transactionData){
            HttpURLConnection conn = null;
            try{
                conn = ApiUtil.fetchApi(
                        "/api/v1/transaction",
                        ApiUtil.RequestMethod.POST,
                        transactionData
                );
                if(conn.getResponseCode() != 200){
                    return false;
                }
                return true;
            }catch(IOException e){
                e.printStackTrace();
            }finally {
                if(conn != null){
                    conn.disconnect();
                }
            }
            return false;
    }

    public static boolean putTransactionCategory(int categoryId, String newCategoryName, String newCategoryColor){
        HttpURLConnection conn = null;
        try{
            conn = ApiUtil.fetchApi(
                    "/api/v1/transaction-category/" +categoryId + "?newCategoryName=" + newCategoryName +
                            "&newCategoryColor=" +newCategoryColor,
                    ApiUtil.RequestMethod.PUT,
                    null
            );

            if(conn.getResponseCode()!=200){
                System.out.println("Error(putTransactionCategory: " + conn.getResponseCode());
                return false;
            }
            return true;
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(conn!=null){
                conn.disconnect();
            }
        }
        return false;
    }

    public static boolean deleteTransactionById(int transactionId){
        HttpURLConnection conn = null;
        try{
            conn = ApiUtil.fetchApi(
                    "/api/v1/transaction/" +transactionId,
                    ApiUtil.RequestMethod.DELETE,
                    null
            );

            if(conn.getResponseCode()!=200){
                System.out.println("Error(deleteTransactionById): " + conn.getResponseCode());
                return false;
            }
            return true;
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(conn!=null){
                conn.disconnect();
            }
        }
        return false;
    }

    public static boolean deleteTransactionCategoryByiD(int categoryId){
        HttpURLConnection conn = null;
        try{
            conn = ApiUtil.fetchApi(
                    "/api/v1/transaction-category/" +categoryId,
                    ApiUtil.RequestMethod.DELETE,
                    null
            );

            if(conn.getResponseCode()!=200){
                System.out.println("Error(deleteTransactionCategory: " + conn.getResponseCode());
                return false;
            }
            return true;
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if(conn!=null){
                conn.disconnect();
            }
        }
        return false;
    }
}
