package com.huungan.shopapp.utils;

public class MessageKeys {
    public static final String LOGIN_SUCCESSFULLY =  "user.login.login_successfully";
    public static final String REGISTER_SUCCESSFULLY =  "user.login.register_successfully";
    public static final String LOGIN_FAILED =  "user.login.login_failed";
    public static final String PASSWORD_NOT_MATCH =  "user.register.password_not_match";
    public static final String USER_IS_LOCKED = "user.login.user_is_locked";
    public static final String WRONG_PHONE_PASSWORD = "user.login.wrong_phone_password";
    public static final String ROLE_DOES_NOT_EXISTS = "user.login.role_not_exist";
    public static final String PHONE_EXISTING = "user.register.phone_existing";
    public static final String USER_NOT_FOUND = "user.find_by_id.not_found";

    public static final String INSERT_CATEGORY_SUCCESSFULLY = "category.create_category.create_successfully";
    public static final String DELETE_CATEGORY_SUCCESSFULLY = "category.delete_category.delete_successfully";
    public static final String UPDATE_CATEGORY_SUCCESSFULLY = "category.update_category.update_successfully";
    public static final String INSERT_CATEGORY_FAILED = "category.create_category.create_failed";

    public static final String CREATE_PRODUCT_SUCCESSFULLY= "product.create_successfully";
    public static final String FIND_PRODUCT_BY_KEYWORD_SUCCESSFULLY= "product.find_by_keyword_successfully";
    public static final String FIND_PRODUCT_BY_IDS_SUCCESSFULLY= "product.find_by_ids_successfully";
    public static final String FIND_PRODUCT_BY_ID_SUCCESSFULLY= "product.find_by_id_successfully";
    public static final String DELETE_PRODUCT_BY_ID_SUCCESSFULLY= "product.delete_by_id_successfully";
    public static final String UPDATE_PRODUCT_BY_ID_SUCCESSFULLY= "product.update_by_id_successfully";

    public static final String DELETE_ORDER_SUCCESSFULLY = "order.delete_order.delete_successfully";
    public static final String DELETE_ORDER_DETAIL_SUCCESSFULLY = "order.delete_order_detail.delete_successfully";
    public static final String FIND_ORDER_BY_ID_SUCCESSFULLY= "order.find_by_id_successfully";
    public static final String CREATE_ORDER_SUCCESSFULLY= "order.create_successfully";
    public static final String FIND_ORDER_BY_ID_NOT_FOUND= "";
    public static final String FIND_ORDER_BY_USER_ID_SUCCESSFULLY="order.find_by_user_id_successfully";

    public static final String UPLOAD_IMAGES_MAX_5 = "product.upload_images.error_max_5_images";
    public static final String UPLOAD_IMAGES_FILE_LARGE = "product.upload_images.file_large";
    public static final String UPLOAD_IMAGES_FILE_MUST_BE_IMAGE = "product.upload_images.file_must_be_image";
    public static final String FIND_PRODUCT_BY_ID_FAILED = "product.find_by_id_not_found";

    public static final String UPLOAD_IMAGES_SUCCESSFULLY= "product_image.upload_successfully";
    public static final String DELETE_IMAGES_SUCCESSFULLY= "product_image.delete_successfully";

}
