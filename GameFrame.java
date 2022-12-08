package java_bingo_game;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class GameFrame extends JFrame{
	Container frame = this.getContentPane();
	FileManager fm = new FileManager();
	Bingo bingo;
	
	// 멤버변수들
	private int n;
	int turn, select;
	static int firstPlayer;
	private int round;	// 라운드 정보 저장
	
	// 해당 단어 위치한 좌표 받아옴
	int[] userCoord;	
	int[] comCoord;
	
	// frame에 들어가는 것들
	String word;
	
	JPanel leftPanel, rightPanel, head, body, currentComStatus;
	JPanel[][] userBoardTiles;
	JLabel comStatusLbl, userInputLbl;
	JTextField currentRating, userRecentSelectWord, comRecentSelectWord, textInput, comBingoNumLbl1, comBingoNumLbl2;
	JButton exitGame;
	
	GameFrame(String title, int n){
		super(title);
		Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        
        this.n = n;
        // 사이즈 조절 -> 전체 화면의 50%정도로 설정
        int width = (int)(screenSize.width * 0.8);
        int height = (int)(screenSize.height * 0.8);
        this.setSize(width, height);
        this.setLocationRelativeTo(null);
        this.addWindowListener(new MyWindowAdapter());
        
//		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		init();
		this.setVisible(true);
	}
	
	class MyWindowAdapter extends WindowAdapter{
    	public void windowClosing(WindowEvent e) {
    		fm.updateStatistics();	// gameCnt 업데이트
    		MainFrame.setCardLayout("startScreen");
    		dispose();
    	}
    }
	
	private void init() {
		// setting : frame layout 설정
		frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
				
		// 빙고판 제작
		bingo = new Bingo(n);	// n 인자로 전달
		bingo.setBingoBoard();	// 빙고판 제작하는 메소드
		
		
		// 멤버변수 초기화
		word = "";
		FileManager.setGameCnt(FileManager.getGameCnt() + 1);
		round = 0;
		
		// 패널 초기화 및 부착
		initPanels();
		
		// adminMode - 관리자 모드일땐 무조건 그냥 사용자 먼저
		if(MainFrame.adminMode) {
			firstPlayer = 0;
		}else {
			firstPlayer = flipCoin();	// 0 - com, 1 - user
		}
		
		// com 먼저면 여기서 com 먼저 한번 선택해줌
		if(firstPlayer == 0) {
			// round 출력	
			System.out.println("------------- round " + ++round + "-----------------");
			System.out.println("first player - " + firstPlayer);
			
			comCoord = bingo.setComBoard();	// com 보드 하나 무작위로 선택, 
			// setComBoard()에서 userBoard에 같은 word 존재하면 userBingoBoard에서 해당 단어의 좌표 반환 { y, x } 형태, coord값 < 0이면 없다는 것
			int y = comCoord[0], x = comCoord[1];
			Word selectedWord = bingo.getComBingoBoard()[y][x];
			
			if(y >= 0) {	
				System.out.println("com 선택 - " + selectedWord.getEng() + "");
				System.out.println("com - comBoard [" + y + ", " + x + "] 선택");
				
				// com - userBoard에서 선택
				int[] userCoord = bingo.setUserBoard(selectedWord);
				y = userCoord[0]; x = userCoord[1];
				if(y > 0) {
					System.out.println("com - userBoard [" + y + ", " + x + "] 선택\n");
					userBoardTiles[y][x].setBackground(Color.white);	// yellow - user, red - com
					userBoardTiles[y][x].revalidate();
					userBoardTiles[y][x].repaint();
					comBingoNumLbl1.setText("다음 빙고까지 남은 개수 - " + bingo.getLeftBingoTileNum(bingo.getComBoard()) + "\n");
				}else {
					System.out.println("com - userBoard 선택 - 선택할 수 있는 타일이 더 이상 없습니다!\n");
				}
			}else {
				System.out.println("com - comBoard 선택 - 선택할 수 있는 타일이 더 이상 없습니다!\n");
				// com - userBoard에서 선택
				int[] userCoord = bingo.setUserBoard(selectedWord);
				y = userCoord[0]; x = userCoord[1];
				if(y > 0) {
					System.out.println("com - userBoard [" + y + ", " + x + "] 선택\n");
					userBoardTiles[y][x].setBackground(Color.white);	// yellow - user, red - com
					userBoardTiles[y][x].revalidate();
					userBoardTiles[y][x].repaint();
					comRecentSelectWord.setText("O - " + bingo.getUserBingoBoard()[y][x] + "를 선택했습니다.");
				}else {
					System.out.println("com - userBoard 선택 - 선택할 수 있는 타일이 더 이상 없습니다!\n");
				}		
			}
		}
	}
	
	// bigo 결과 처리 부분
	private void finishGame(int result) {
		// 승/패 결정
		String resultMsg = "";
		if(result == 1) {	// 1 -> com win
			FileManager.setComWinCnt(FileManager.getComWinCnt() + 1);
			resultMsg = "컴퓨터 승!\n";
			if(FileManager.getComWinRate() > 0.7) {
				resultMsg += "??? : 인간 시대의 끝이 도래했다\n";
			}
		}else if(result == 2){					// 2 -> user win
			FileManager.setUserWinCnt(FileManager.getUserWinCnt() + 1);
			resultMsg = "User - 승!\n";
			if(FileManager.getComWinRate() < 0.3) {
				resultMsg += "??? : Alphago_resign\n";
			}
		}else if(result == 3) {
			resultMsg = "무승부 - 더 이상 채울 빙고 칸이 없습니다!\n";
		}
		
		// 결과 반영 및 저장
		fm.updateStatistics();
		resultMsg += "통계:\n";
		resultMsg += "user 이긴 횟수 - " + FileManager.getUserWinCnt() + "\n";
		resultMsg += "com 이긴 횟수 - " + FileManager.getComWinCnt() + "\n";
		
		resultMsg += "user 승률 - " + (int)(FileManager.getUserWinRate() * 10000) / (double)100 + "%\n";
		resultMsg += "com 승률 - " + (int)(FileManager.getComWinRate() * 10000) / (double)100 + "%\n";
		message(resultMsg, JOptionPane.INFORMATION_MESSAGE);
		
		MainFrame.setCardLayout("startScreen");
		dispose();
	}
	
	private void initPanels() {
		// setting : 게임 화면 패널들 설정
		Dimension frDim = this.getSize();
		Dimension panelDim;
//		System.out.println("frame width, height - " + frDim.width + " " + frDim.height);
		
		// 화면 구성, 패널들 설정
		leftPanel = new JPanel(new BorderLayout(0, 0));
		leftPanel.setPreferredSize(new Dimension((int) (frDim.width * 0.7), (int)(frDim.height * 0.9)));
		panelDim = leftPanel.getSize();
		leftPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "BINGO BOARD"));
		
//		rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, frDim.width));
		rightPanel = new JPanel();
		rightPanel.setBorder(new TitledBorder(new LineBorder(Color.BLACK), "BINGO STATUS"));
//		rightPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 100, 10));
		rightPanel.setLayout(new FlowLayout());
		
		rightPanel.setPreferredSize(new Dimension((int)(frDim.width * 0.25), (int)(frDim.height * 0.9)));
		head = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		head.setPreferredSize(new Dimension(500, 100));
		body = new JPanel();
		
		// init leftPanel Components
			// head			
			currentRating = new JTextField(20);
			currentRating.setBorder(new TitledBorder(new LineBorder(Color.black), "user 의 승률"));
			currentRating.setText(((int)(FileManager.getUserWinRate() * 10000)) / (double)100 + "%");
			currentRating.setEditable(false);
			
			userRecentSelectWord = new JTextField(20);
			userRecentSelectWord.setBorder(new TitledBorder(new LineBorder(Color.black), "user 선택 단어"));
			userRecentSelectWord.setEditable(false);
			
			comRecentSelectWord = new JTextField(20);
			comRecentSelectWord.setBorder(new TitledBorder(new LineBorder(Color.black), "com 선택 단어"));
			comRecentSelectWord.setEditable(false);
			
			
			head.add(currentRating);
			head.add(userRecentSelectWord);
			head.add(comRecentSelectWord);
			head.setPreferredSize(new Dimension(panelDim.width, 100));
			
			// body
			body.setLayout(new GridLayout(n, n));
			userBoardTiles = new JPanel[n][n];
			for(int i = 0; i < n; i++) {
				userBoardTiles[i] = new JPanel[n];
				for(int j = 0; j < n; j++) {
					JLabel text = new JLabel(bingo.getUserBingoBoard()[i][j].getEng());
					userBoardTiles[i][j] = new JPanel();
					userBoardTiles[i][j].setBorder(new LineBorder(Color.black));
					userBoardTiles[i][j].setPreferredSize(new Dimension((int)(panelDim.width / n), (int)(panelDim.width / n)));
					text.setPreferredSize(new Dimension(100, 100));
					
					userBoardTiles[i][j].add(text);
					body.add(userBoardTiles[i][j]);
				}
			}
		leftPanel.add(head, BorderLayout.NORTH);
		leftPanel.add(body, BorderLayout.CENTER);
		
		// init rightPanel
			userInputLbl = new JLabel();
			userInputLbl.setText("단어를 입력하세요 : ");
			
			// actionListener
			textInput = new JTextField(10);		// textInput 입력 부분  ---------------
			textInput.addActionListener(e->{	// action : user bingo selection
				// user -> com이라면 com차례시 해야 할 것들 실행
				if(firstPlayer == 1) {
					// round 출력	
					System.out.println("------------- round " + ++round + "-----------------");
					System.out.println("first player - " + firstPlayer);
					userTurn();
					updateComBingoStatus();
					comTurn();
					updateComBingoStatus();
					int result = bingo.checkBingo();
					if(result != 0) {
						finishGame(result);
					}
				}else {
					// 빙고 현황 확인, 최종 판정
					userTurn();
					updateComBingoStatus();
					int result = bingo.checkBingo();
					if(result != 0) {
						finishGame(result);
					}
					// round 출력	
					System.out.println("------------- round " + ++round + "-----------------");
					System.out.println("first player - " + firstPlayer);
					comTurn();
					updateComBingoStatus();
				}
				
				
			});
			
			initComBingoStatus();
			initExitButton();
		
		rightPanel.add(userInputLbl);
		rightPanel.add(textInput);
		rightPanel.add(currentComStatus);
		rightPanel.add(exitGame);
		
		frame.add(leftPanel);
		frame.add(rightPanel);
		
	}
	
	
	private void userTurn() {
		// user입력
		word = textInput.getText();
		word = word.trim();
		textInput.setText("");		// clears textInput
		userRecentSelectWord.setText("");
		comRecentSelectWord.setText("");
		
		userCoord = bingo.setUserBoard(word);	// 해당 단어 위치한 좌표 받아옴
		
		// 만약 해당 단어가 있다면
		if(userCoord[0] >= 0) {
			int y = userCoord[0];
			int x = userCoord[1];
			userRecentSelectWord.setText("O - " + bingo.getUserBingoBoard()[y][x] + "를 선택하셨습니다.");
			userBoardTiles[y][x].setBackground(Color.YELLOW);	// color : user -> user 선택 yellow - user, red - com 
			userBoardTiles[y][x].revalidate();
			userBoardTiles[y][x].repaint();
			
			// 선택 단어 좌표 출력 부분
//			if(MainFrame.adminMode) {
				System.out.println("user 선택 - " + word + "\n");
				System.out.println("user - userBoard [" + y + ", " + x + "] 선택");
//			}
			
		}else{
			userRecentSelectWord.setText("");
//			if(MainFrame.adminMode) {
				System.out.println("\"" + word + "\"는 빙고판에 없습니다.");
//			}
		}
		
		// com에도 있다면 add, com board 좌표 반환
		comCoord = bingo.setComBoard(word);
		if(comCoord[0] >= 0) {
			System.out.println("user - comBoard [" + comCoord[0] + ", " + comCoord[1] + "] 선택");
		}else {
			System.out.println("user - comBoard 에서 선택할 수 있는 단어가 없습니다.");
		}
	}
	
	private void comTurn() {
		// 사람 먼저 - com 선택으로 가게 -> 그 다음에 checkBingo()
		comCoord = bingo.setComBoard();
		int y = comCoord[0], x = comCoord[1];
		
		if(y >= 0) {
			System.out.println("com 선택 - " + bingo.getComBingoBoard()[y][x].getEng() + "\n");
			System.out.println("com - comBoard [" + y + ", " + x + "] 선택");
		}else {
			System.out.println("com - comBoard 선택 - 선택할 수 있는 타일이 더 이상 없습니다!");
		}
		
		if(y >= 0) {
			Word selectedWord = bingo.getComBingoBoard()[y][x];
			userCoord = bingo.setUserBoard(selectedWord);
			y = userCoord[0]; 
			x = userCoord[1];
			if(y >= 0) {
				comRecentSelectWord.setText("O - " + bingo.getUserBingoBoard()[y][x] + "를 선택하셨습니다.");
				userBoardTiles[y][x].setBackground(Color.WHITE); 	// color : com -> user 선택
				userBoardTiles[y][x].revalidate();
				userBoardTiles[y][x].repaint();
				
//						if(MainFrame.adminMode) {
					System.out.println("com - userBoard [" + userCoord[0] + ", " + userCoord[1] + "] 선택");
//						}
			}else {
				System.out.println("com - userBoard 선택 - 선택할 수 있는 타일이 더 이상 없습니다!");
			}
		}
	}
	
	private void updateComBingoStatus() {
		// com 빙고 현황 업데이트
		String comBingoStatus_maxNum = "", comBingoStatus_status = "";
		comBingoStatus_maxNum = "다음 빙고까지 남은 칸 개수 - " + bingo.getLeftBingoTileNum(bingo.getComBoard()) + "\n";
		comBingoStatus_status = "빙고 개수 - " + bingo.getBingoNum(bingo.getComBoard(), bingo.getComBingoBoard(), "updateBingoStatus - com") + " / " + (2 * n + 2);
		
		comBingoNumLbl1.setText(comBingoStatus_maxNum);
		comBingoNumLbl2.setText(comBingoStatus_status);
	}
	
	private void initComBingoStatus() {
		// com 빙고 현황 초기화
		String comBingoStatus_maxNum = "";
		String comBingoStatus_status = "";
		currentComStatus = new JPanel();
		currentComStatus.setLayout(new GridLayout(2, 1));
		comBingoNumLbl1 = new JTextField(20);
		comBingoNumLbl1.setBorder(new TitledBorder(new LineBorder(Color.black), "com"));
		comBingoNumLbl2 = new JTextField(20);
		comBingoNumLbl2.setBorder(new TitledBorder(new LineBorder(Color.black), "com"));
		
		comBingoNumLbl1.setEditable(false);
		comBingoNumLbl2.setEditable(false);
		
		comBingoStatus_maxNum = "다음 빙고까지 남은 개수 - " + bingo.getLeftBingoTileNum(bingo.getComBoard()) + "\n";
		comBingoStatus_status = "빙고 진행도 - " + bingo.getBingoNum(bingo.getComBoard(), bingo.getComBingoBoard(), "action - com 빙고") + " / " + (2 * n + 2);
		
		comBingoNumLbl1.setText(comBingoStatus_maxNum);
		comBingoNumLbl2.setText(comBingoStatus_status);
		// comBingoStatus - com status 보여주는 패널
		currentComStatus.add(comBingoNumLbl1);
		currentComStatus.add(comBingoNumLbl2);		
	}
	
	private void initExitButton() {
		exitGame = new JButton("게임 종료");
		exitGame.addActionListener(e->{
			// action : 빙고 게임 종료
			try {
				int returnVal = JOptionPane.showConfirmDialog(null, "게임을 끝내시겠습니까?", "Notice", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				
				if(returnVal == JOptionPane.YES_OPTION) {
					fm.updateStatistics();	// gameCnt 업데이트
					MainFrame.setCardLayout("startScreen");
					dispose();
				}
			}catch(Exception e1) {
				System.out.println(e1);
			}
		});
	}
	
	
	private int flipCoin() {
		String[] answers = {"앞면", "뒷면", "아무거나"};
		Random r = new Random();
		int coin = r.nextInt(2);
		int userInput = JOptionPane.showOptionDialog(null, "컴퓨터가 엄청난 알고리즘을 이용해 동전을 던집니다...\n"
				+ "동전의 앞뒷면을 맞추면 먼저 시작합니다.\n"
				+ "답을 골라주세요.", "Notice", 
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, answers, null);
		
		if(userInput == 2) userInput = r.nextInt(2);	// 랜덤으로 결정
		
		if(coin == userInput) {
			JOptionPane.showMessageDialog(null, "맞췄습니다!\nuser가 먼저 게임을 시작합니다.");
			return 1;
		}
		else {
			JOptionPane.showMessageDialog(null, "아.. 틀렸습니다!\n" + "컴퓨터가 먼저 게임을 시작합니다.");
			return 0;
		}
	}
	
	
	public static void message(String msg, int msgType) {
		JOptionPane.showMessageDialog(null, msg, "Notice", msgType);
	}
}
