--
-- PostgreSQL database dump
--

-- Dumped from database version 14.18
-- Dumped by pg_dump version 14.18

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: analysis_results; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.analysis_results (
    id bigint NOT NULL,
    analysis_session_id bigint NOT NULL,
    file_id bigint NOT NULL,
    class_name text,
    severity text NOT NULL,
    issue text NOT NULL,
    suggestions text,
    analyzed_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.analysis_results OWNER TO postgres;

--
-- Name: analysis_results_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.analysis_results_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.analysis_results_id_seq OWNER TO postgres;

--
-- Name: analysis_results_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.analysis_results_id_seq OWNED BY public.analysis_results.id;


--
-- Name: analysis_sessions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.analysis_sessions (
    id bigint NOT NULL,
    repository_id bigint NOT NULL,
    model_name character varying(255) NOT NULL,
    prompt text,
    executed_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.analysis_sessions OWNER TO postgres;

--
-- Name: analysis_sessions_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.analysis_sessions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.analysis_sessions_id_seq OWNER TO postgres;

--
-- Name: analysis_sessions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.analysis_sessions_id_seq OWNED BY public.analysis_sessions.id;


--
-- Name: file_content_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.file_content_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_content_seq OWNER TO postgres;

--
-- Name: files; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.files (
    id bigint NOT NULL,
    repository_id bigint NOT NULL,
    path character varying(255) NOT NULL,
    content text,
    type character varying(255) NOT NULL,
    sha_code character varying(255) NOT NULL,
    is_active boolean DEFAULT true NOT NULL
);


ALTER TABLE public.files OWNER TO postgres;

--
-- Name: files_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.files_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.files_id_seq OWNER TO postgres;

--
-- Name: files_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.files_id_seq OWNED BY public.files.id;


--
-- Name: reports; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reports (
    id bigint NOT NULL,
    analysis_session_id bigint NOT NULL,
    generated_at timestamp with time zone NOT NULL,
    file_path character varying(255) NOT NULL
);


ALTER TABLE public.reports OWNER TO postgres;

--
-- Name: reports_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.reports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.reports_id_seq OWNER TO postgres;

--
-- Name: reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.reports_id_seq OWNED BY public.reports.id;


--
-- Name: repositories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.repositories (
    id bigint NOT NULL,
    user_name character varying(255) NOT NULL,
    repo_name character varying(255) NOT NULL,
    url character varying(255),
    created_at date DEFAULT now() NOT NULL,
    up_to_date boolean DEFAULT true NOT NULL,
    branch_name character varying(255) NOT NULL
);


ALTER TABLE public.repositories OWNER TO postgres;

--
-- Name: repositories_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.repositories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.repositories_id_seq OWNER TO postgres;

--
-- Name: repositories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.repositories_id_seq OWNED BY public.repositories.id;


--
-- Name: analysis_results id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_results ALTER COLUMN id SET DEFAULT nextval('public.analysis_results_id_seq'::regclass);


--
-- Name: analysis_sessions id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_sessions ALTER COLUMN id SET DEFAULT nextval('public.analysis_sessions_id_seq'::regclass);


--
-- Name: files id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.files ALTER COLUMN id SET DEFAULT nextval('public.files_id_seq'::regclass);


--
-- Name: reports id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reports ALTER COLUMN id SET DEFAULT nextval('public.reports_id_seq'::regclass);


--
-- Name: repositories id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.repositories ALTER COLUMN id SET DEFAULT nextval('public.repositories_id_seq'::regclass);


--
-- Name: analysis_results analysis_results_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_results
    ADD CONSTRAINT analysis_results_pkey PRIMARY KEY (id);


--
-- Name: analysis_sessions analysis_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_sessions
    ADD CONSTRAINT analysis_sessions_pkey PRIMARY KEY (id);


--
-- Name: files files_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT files_pkey PRIMARY KEY (id);


--
-- Name: reports reports_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (id);


--
-- Name: repositories repositories_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.repositories
    ADD CONSTRAINT repositories_pkey PRIMARY KEY (id);


--
-- Name: analysis_results fk_analysis_session_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_results
    ADD CONSTRAINT fk_analysis_session_id FOREIGN KEY (analysis_session_id) REFERENCES public.analysis_sessions(id) NOT VALID;


--
-- Name: reports fk_analysis_session_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT fk_analysis_session_id FOREIGN KEY (analysis_session_id) REFERENCES public.analysis_sessions(id) NOT VALID;


--
-- Name: analysis_results fk_file_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_results
    ADD CONSTRAINT fk_file_id FOREIGN KEY (file_id) REFERENCES public.files(id) NOT VALID;


--
-- Name: files fk_repository_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT fk_repository_id FOREIGN KEY (repository_id) REFERENCES public.repositories(id) NOT VALID;


--
-- Name: analysis_sessions fk_repository_id ; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.analysis_sessions
    ADD CONSTRAINT "fk_repository_id " FOREIGN KEY (repository_id) REFERENCES public.repositories(id) NOT VALID;


--
-- PostgreSQL database dump complete
--

