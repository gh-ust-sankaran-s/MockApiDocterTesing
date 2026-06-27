CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(255),
    status VARCHAR(50),
    total DECIMAL(10,2),
    user_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO orders (order_number, status, total, user_id)
VALUES
('O-1001', 'CREATED', 100.00, 'user-1'),
('O-1002', 'Confirmed', 200.00, 'user-2');
