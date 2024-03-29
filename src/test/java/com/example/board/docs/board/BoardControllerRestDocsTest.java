package com.example.board.docs.board;

import com.example.board.controller.BoardController;
import com.example.board.docs.RestDocsTemplate;
import com.example.board.dto.BoardListResponse;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.CreateBoardRequest;
import com.example.board.dto.CreateBoardResponse;
import com.example.board.service.BoardService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BoardControllerRestDocsTest extends RestDocsTemplate {

	private final BoardService boardService = mock(BoardService.class);

	@Override
	protected Object initController() {
		return new BoardController(boardService);
	}

	@DisplayName("전체 게시글을 조회하는 API")
	@Test
	void 전체_게시글을_조회하는_API() throws Exception {
		List<BoardListResponse> response = Lists.newArrayList(
				new BoardListResponse(1L, "제목1", 0, LocalDateTime.now()),
				new BoardListResponse(2L, "제목2", 0, LocalDateTime.now()),
				new BoardListResponse(3L, "제목3", 0, LocalDateTime.now()),
				new BoardListResponse(4L, "제목4", 0, LocalDateTime.now()),
				new BoardListResponse(5L, "제목5", 0, LocalDateTime.now())
		);

		when(boardService.findAll()).thenReturn(response);

		mockMvc.perform(get("/api/board"))
				.andExpect(status().isOk())
				.andDo(document("boards/get-all",
						preprocessResponse(prettyPrint()),
						responseFields(
								fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("게시글 ID"),
								fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글 제목"),
								fieldWithPath("[].viewCnt").type(JsonFieldType.NUMBER).description("게시글 조회수"),
								fieldWithPath("[].createAt").type(JsonFieldType.STRING).description("게시글 작성일")
						)
				));
	}

	@DisplayName("단건 게시글을 조회하는 API")
	@Test
	void 단건_게시글을_조회하는_API() throws Exception {
		CreateBoardRequest createBoardRequest = CreateBoardRequest.builder()
				.title("제목1")
				.cont("내용1")
				.build();

		CreateBoardResponse createBoardResponse = CreateBoardResponse.builder()
				.id(1L)
				.build();

		BoardResponse response = BoardResponse.builder()
				.id(1L)
				.title("제목1")
				.cont("내용1")
				.viewCnt(0)
				.createAt(LocalDateTime.now())
				.build();

		when(boardService.save(createBoardRequest)).thenReturn(createBoardResponse);
		when(boardService.findById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/board/{id}", 1))
				.andExpect(status().isOk())
				.andDo(document("boards/get",
						preprocessResponse(prettyPrint()),
						responseFields(
								fieldWithPath("id").type(JsonFieldType.NUMBER).description("id"),
								fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
								fieldWithPath("cont").type(JsonFieldType.STRING).description("내용"),
								fieldWithPath("viewCnt").type(JsonFieldType.NUMBER).description("조회수"),
								fieldWithPath("createAt").type(JsonFieldType.STRING).description("등록일")
						)
				));
	}

	@DisplayName("게시글 작성 API")
	@Test
	void 게시글_작성_API() throws Exception {
		CreateBoardResponse createBoardResponse = CreateBoardResponse.builder()
				.id(1L)
				.build();

		when(boardService.save(any(CreateBoardRequest.class))).thenReturn(createBoardResponse);

		mockMvc.perform(post("/api/board/")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								 {
								 	"title": "제목1",
								 	"cont": "내용1"
								 }
								"""))
				.andExpect(status().isOk())
				.andDo(document("boards/create",
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						requestFields(
								fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
								fieldWithPath("cont").type(JsonFieldType.STRING).description("내용")
						),
						responseFields(
								fieldWithPath("id").type(JsonFieldType.NUMBER).description("id")
						)
				));
	}

}
