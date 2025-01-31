CREATE TABLE public.audit_log_entries (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    action character varying,
    subject_type character varying NOT NULL,
    subject_id uuid NOT NULL,
    username character varying NOT NULL,
    updates jsonb,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);