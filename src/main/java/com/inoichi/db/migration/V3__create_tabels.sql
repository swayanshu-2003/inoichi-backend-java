-- Fix foreign key reference in user_queries
ALTER TABLE app.user_queries
DROP CONSTRAINT user_queries_user_id_fkey,
ADD CONSTRAINT user_queries_user_id_fkey
FOREIGN KEY (user_id) REFERENCES app.users(id) ON DELETE CASCADE;
