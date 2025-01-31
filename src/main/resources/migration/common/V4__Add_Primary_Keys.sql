ALTER TABLE public.local_delivery_unit_mailboxes ALTER COLUMN id SET NOT NULL;
ALTER TABLE public.local_delivery_unit_mailboxes ADD PRIMARY KEY (id);
ALTER TABLE public.offender_management_unit_mailboxes ALTER COLUMN id SET NOT NULL;
ALTER TABLE public.offender_management_unit_mailboxes ADD PRIMARY KEY (id);