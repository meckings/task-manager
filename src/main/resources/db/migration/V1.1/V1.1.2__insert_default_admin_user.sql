INSERT IGNORE INTO users (email, password) VALUES ("test@test.com", "$2a$10$jSFnBVYcXJdF54fJ0mRkeeXo4z2/A00v8jKR0JnU4dnqLDWAWGZ66"); #--password is `password`

#-- Retrieve the last inserted user ID
SELECT id INTO @userId FROM users WHERE email = "test@test.com";

#-- Retrieve the role IDs"
SELECT id INTO @adminRoleId FROM roles WHERE name = "ROLE_ADMIN";
SELECT id INTO @userRoleId FROM roles WHERE name = "ROLE_USER";

#-- Insert the user ID and role ID into the user_roles table
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (@userId, @adminRoleId);
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (@userId, @userRoleId);
