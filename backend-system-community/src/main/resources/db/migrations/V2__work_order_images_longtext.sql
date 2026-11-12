-- Base64-encoded resident-uploaded images can exceed MySQL TEXT (64 KiB).
-- This only expands the column capacity; it preserves all existing work-order rows.
ALTER TABLE work_order MODIFY COLUMN images LONGTEXT NULL;
