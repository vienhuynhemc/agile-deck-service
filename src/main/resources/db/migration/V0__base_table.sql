--
-- TOC entry 185 (class 1259 OID 16491)
-- Name: tbl_answer_groups; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tbl_answer_groups (
    id BIGSERIAL PRIMARY KEY,
    name character varying(255)
);


--
-- TOC entry 186 (class 1259 OID 16494)
-- Name: tbl_answered_question_details; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tbl_answered_question_details (
    id BIGSERIAL PRIMARY KEY,
    answered_question_id bigint,
    player_id bigint,
    answer_content text,
    answer_content_as_description text NULL,
    answer_content_as_image character varying(255)
);


--
-- TOC entry 187 (class 1259 OID 16500)
-- Name: tbl_answered_questions; Type: TABLE; Schema: public; Owner: -
--


CREATE TABLE public.tbl_answered_questions (
    id BIGSERIAL PRIMARY KEY,
    game_board_id bigint NOT NULL,
    question_content text,
    question_content_as_image character varying(255),
    answer_content text,
    answer_content_as_description text NULL,
    answer_content_as_image character varying(255),
    playing boolean,
    isPlayed boolean,
    question_id bigint
);


--
-- TOC entry 188 (class 1259 OID 16506)
-- Name: tbl_answers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tbl_answers (
    id BIGSERIAL PRIMARY KEY,
    answer_content text,
    answer_content_as_description text NULL,
    number_order bigint NOT NULL,
    answer_content_as_image character varying(255),
    answer_group_id bigint NOT NULL,
    game_id bigint,
    question_id bigint
);


--
-- TOC entry 189 (class 1259 OID 16512)
-- Name: tbl_game_boards; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tbl_game_boards (
    id BIGSERIAL PRIMARY KEY,
    code character varying(50) NOT NULL,
    game_id bigint NOT NULL
);


CREATE TABLE public.tbl_custom_answers (
    id BIGSERIAL PRIMARY KEY,
    answer_content text,
    answer_content_as_description text NULL,
    number_order bigint NOT NULL,
    answer_content_as_image character varying(255),
    game_board_id bigint
);


--
-- TOC entry 190 (class 1259 OID 16515)
-- Name: tbl_games; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tbl_games (
    id BIGSERIAL PRIMARY KEY,
    description character varying(255),
    name character varying(255),
    game_as_image character varying(255),
    question_title character varying(255),
    answer_title character varying(255),
    player_title character varying(255),
    image_player_start character varying(255),
    image_backside character varying(255),
    editable boolean,
    additional_possibility boolean,
    random_when_next boolean
);



--
-- TOC entry 191 (class 1259 OID 16521)
-- Name: tbl_players; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tbl_players (
    id BIGSERIAL PRIMARY KEY,
    game_board_id bigint NOT NULL,
    avatar character varying(255),
    name character varying(255) NOT NULL
);


--
-- TOC entry 192 (class 1259 OID 16527)
-- Name: tbl_questions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tbl_questions (
    id BIGSERIAL PRIMARY KEY,
    question_content text,
    question_content_as_image character varying(255),
    game_board_id bigint,
    game_id bigint
);


--
-- TOC entry 2154 (class 0 OID 16491)
-- Dependencies: 185
-- Data for Name: tbl_answer_groups; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tbl_answer_groups (name)
VALUES('Approach set'),
    ('Solution for New Deck');


--
-- TOC entry 2156 (class 0 OID 16500)
-- Dependencies: 187
-- Data for Name: tbl_answered_questions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tbl_answered_questions
(game_board_id, question_content, playing,isPlayed)
VALUES(1, 'Building a new highway', true,false),
    (1, 'Learning to ride a horse', false,false),
    (2, 'Sewing a patchwork quilt', false,true),
    (2, 'Negotiating the release of a kidnapped person', true,false);



--
-- TOC entry 2157 (class 0 OID 16506)
-- Dependencies: 188
-- Data for Name: tbl_answers; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tbl_answers (answer_content, answer_content_as_description, number_order, answer_content_as_image, answer_group_id, game_id)
VALUES ('Iterative','', 1, 'iib_iterative.png', 1, 1),
    ('Incremental','', 2, 'iib_incremental.png', 1, 1),
    ('Bigbang','', 3, 'iib_bigbang.png', 1, 1),
    ('Yes','Yes', 1, 'default_answer.png', 2, 2),
    ('No','No', 2, 'default_answer.png', 2, 2);

--
-- TOC entry 2155 (class 0 OID 16494)
-- Dependencies: 186
-- Data for Name: tbl_answered_question_details; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tbl_answered_question_details
(answered_question_id, player_id, answer_content_as_image)
VALUES(1,1,'iterative.png'),
    (1,2,'bigbang.png'),
    (1,3,'incremental.png'),
    (1,4,'iterative.png'),
    (1,5, null);


--
-- TOC entry 2158 (class 0 OID 16512)
-- Dependencies: 189
-- Data for Name: tbl_game_boards; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tbl_game_boards (code, game_id)
VALUES('e3bb8a9d-704e-430e-acae-1fb0a9695207', 1),
        ( 'asd6gfga-f296-sdf3-0fn2-asf86gc1crt2', 1),
        ( 'e3bb8a9d-704e-430e-acae-1fb0a9695205', 1),
        ( 'e3bb8a9d-704e-430e-acae-1fb0a96rtfu8', 1),
        ( 'e3bb8a9d-704e-430e-acae-1fb0a96dsy76', 1);


--
-- TOC entry 2159 (class 0 OID 16515)
-- Dependencies: 190
-- Data for Name: tbl_games; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tbl_games(name, description,game_as_image, question_title, answer_title, player_title, image_player_start, image_backside, editable, additional_possibility, random_when_next)
VALUES ('Iterative - Incremental - Big Bang', 'A workshop game to encourage people to think about alternative approaches for tackling projects. - by Scrum & Kanban','iib_home.png', 'Select a scenario', 'Pick an approach', 'Players', 'iib_playerstart.png', 'iib_backside.png', false, false, true),
        ('New Deck', 'The objective of the game is to make a decision as to how to best maximize the profit of this process','nd_home.png', 'Pick a problem', 'Choose an answer', 'Players', 'nd_playerstart.png', 'nd_backside.png', true, true, false);

--
-- TOC entry 2160 (class 0 OID 16521)
-- Dependencies: 191
-- Data for Name: tbl_players; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tbl_players
(name, game_board_id)
VALUES('Strawberries', 1),
    ('Banana', 1),
    ('Orange', 1),
    ('Mango', 1),
    ('Durian', 1);


--
-- Insert into table custom answer to run test case

INSERT INTO public.tbl_custom_answers 
(answer_content, answer_content_as_description, answer_content_as_image, number_order, game_board_id)
VALUES ('a', '', 'a.png', 1, 2),
('b', '', 'a.png', 1, 2),
('c', '', 'a.png', 1, 2),
('d', '', 'a.png', 1, 2),
('e', '', 'a.png', 1, 2),
('f', '', 'a.png', 1, 2),
('g', '', 'a.png', 1, 2),
('h', '', 'a.png', 1, 2),
('j', '', 'a.png', 1, 2),
('k', '', 'a.png', 1, 2),
('l', '', 'a.png', 1, 2),
('q', '', 'a.png', 1, 2),
('w', '', 'a.png', 1, 2),
('y', '', 'a.png', 1, 2),
('r', '', 'a.png', 1, 2);

--
-- TOC entry 2161 (class 0 OID 16527)
-- Dependencies: 192
-- Data for Name: tbl_questions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.tbl_questions
(question_content, game_id)
VALUES('Building a new highway', 1),
    ('Learning to ride a horse', 1),
    ('Sewing a patchwork quilt', 1),
    ('Negotiating the release of a kidnapped person', 1),
    ('Learning to speak in public', 1),
    ('Learning to dive', 1),
    ('Building a tree house', 1),
    ('Organizing a party', 1),
    ('Laying a pavement', 1),
    ('Adopting a child', 1),
    ('Knitting a scarf', 1),
    ('Moving house', 1),
    ('Learning a new language', 1),
    ('Growing a beard', 1),
    ('Wring a wedding speech', 1),
    ('Making wine', 1),
    ('Converting a loft', 1),
    ('Making a table', 1),
    ('Writing a song', 1),
    ('Solving an equation', 1),
    ('Pruning a tree', 1),
    ('Making a clay vase', 1),
    ('Building a bridge', 1),
    ('Renovating a house', 1),
    ('Creating a website to sell pet products', 1),
    ('Training to run a marathon', 1),
    ('Writing a book', 1),
    ('Making a Hollywood blockbuster', 1),
    ('Losing weight', 1),
    ('Cooking Sunday lunch', 1),
    ('Building a new house', 1),
    ('Building an extension', 1),
    ('Building a nuclear submarine', 1),
    ('Putting a person on the moon', 1),
    ('Laying a new lawn', 1),
    ('Raising money for charity', 1),
    ('Becoming a bodybuilder', 1),
    ('Learning to play the guitar', 1),
    ('Planning a wedding', 1),
    ('Growing a vegetable patch', 1),
    ('Designing a poster', 1),
    ('Landscaping a garden', 1),
    ('Decorating a house', 1),
    ('Having a baby', 1),
    ('Building a car', 1),
    ('Problem1', 2);


-- Completed on 2020-12-03 16:27:24

--
-- PostgreSQL database dump complete
--

