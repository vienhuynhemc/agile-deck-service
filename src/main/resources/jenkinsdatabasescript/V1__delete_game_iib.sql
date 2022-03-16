-- delete iib game on table game
DELETE FROM public.tbl_games WHERE id = 1;

-- delete answer group of iib game
DELETE FROM public.tbl_answer_groups WHERE id = 1;

-- delete game board of iib game
DELETE FROM public.tbl_game_boards WHERE game_id = 1;

-- delete answer question of game board of iib game
DELETE FROM public.tbl_answered_questions WHERE game_board_id = 1;

-- delete answer of iib game
DELETE FROM public.tbl_answers WHERE game_id = 1;

-- delete answer question detail of iib game
DELETE FROM public.tbl_answered_question_details WHERE answered_question_id = 1;

-- delete player of iib game
DELETE FROM public.tbl_players WHERE game_board_id = 1;

-- delete question of iib game
DELETE FROM public.tbl_questions WHERE game_id = 1;