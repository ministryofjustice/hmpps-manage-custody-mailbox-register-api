CREATE TABLE public.offender_management_unit_mailboxes (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    name character varying,
    email_address character varying NOT NULL,
    prison_code character varying NOT NULL,
    role character varying NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);