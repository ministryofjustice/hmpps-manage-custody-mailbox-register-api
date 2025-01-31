CREATE TABLE public.local_delivery_unit_mailboxes (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    name character varying,
    unit_code character varying NOT NULL,
    area_code character varying NOT NULL,
    email_address character varying NOT NULL,
    country character varying,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);