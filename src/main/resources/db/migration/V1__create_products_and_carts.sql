CREATE TABLE products (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE carts (
    id BINARY(16) NOT NULL PRIMARY KEY,
    date_created DATE NOT NULL
);

CREATE TABLE cart_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    cart_id BINARY(16) NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    CONSTRAINT uk_cart_product UNIQUE (cart_id, product_id),
    CONSTRAINT fk_cart_items_cart FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_cart_item_quantity CHECK (quantity BETWEEN 1 AND 100)
);
