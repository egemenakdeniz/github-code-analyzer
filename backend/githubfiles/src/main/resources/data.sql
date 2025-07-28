INSERT INTO users (username, password)
VALUES ('admin', '$2a$10$HfHF7lfAHOi6Pz3bTriUl.fqoR46gtYJ2RmiFGPb8T8eztKI1R.tO')
    ON CONFLICT (username) DO NOTHING;