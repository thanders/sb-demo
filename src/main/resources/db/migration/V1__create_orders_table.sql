CREATE SEQUENCE order_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE orders (
    id           NUMBER DEFAULT order_seq.NEXTVAL PRIMARY KEY,
    product_name VARCHAR2(255),
    price        NUMBER(19, 2)
);
