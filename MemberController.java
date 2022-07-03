package com.kh.test.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kh.test.member.model.vo.Member;

/**
 * @author sangmi
 *
 */

@Controller
public class MemberController {
	/**
	 *  로그인 처리를 해주는 메소드 -오씨
	 * @param m => 로그인할 사용자의 계정 (아이디, 비번)
	 * @return => 메인페이지로 url 재요청
	 */
		@RequestMapping("login.me")
		public String loginMember(Member m) {
			
			return "redirect:/"; //메인페이지 재요청 
		}
		/**
		 *  회원가입  처리를 해주는 메소드 -김씨
		 * @param m 
		 * @return 
		 */
		
		@RequestMapping("insert.me")
		public String insertMember(Member m) {
			return "redirect:/";
		}
		
		
	}
	
	

