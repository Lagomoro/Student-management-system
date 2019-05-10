package pers.lagomoro.stusystem.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import java.util.LinkedHashMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.AlignmentAction;
import javax.swing.text.StyledEditorKit.BoldAction;
import javax.swing.text.StyledEditorKit.ItalicAction;
import javax.swing.text.StyledEditorKit.UnderlineAction;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import pers.lagomoro.stusystem.client.controller.GraphicsController;
import pers.lagomoro.stusystem.client.controller.SceneController;
import pers.lagomoro.stusystem.client.model.LanguageModel;
import pers.lagomoro.stusystem.client.view.component.Scene;
import pers.lagomoro.stusystem.client.view.component.ScrollPane;
import pers.lagomoro.stusystem.client.view.module.FontColorAction;
import pers.lagomoro.stusystem.client.view.module.FontFaceAction;
import pers.lagomoro.stusystem.client.view.module.FontSizeAction;
import pers.lagomoro.stusystem.client.view.module.StrikeThroughAction;
import pers.lagomoro.stusystem.client.view.module.SubscriptAction;
import pers.lagomoro.stusystem.client.view.module.SuperscriptAction;
import pers.lagomoro.stusystem.client.view.widget.ButtonStyled;
import pers.lagomoro.stusystem.client.view.widget.EditTextUserName;
import pers.lagomoro.stusystem.client.view.widget.TextPaneHTMLEditor;
import pers.lagomoro.stusystem.client.view.widget.TextView;
import pers.lagomoro.stusystem.client.view.widget.ViewCombo;
import pers.lagomoro.stusystem.client.view.widget.ViewStyled;

@SuppressWarnings("serial")
public class Scene_NoticeEditor extends Scene{

	protected UndoableEditListener undoHandler; 
	protected UndoManager undoManager; 
	protected UndoAction undoAction;
	protected RedoAction redoAction;

	protected BoldAction boldAction; 
	protected ItalicAction italicAction; 
	protected UnderlineAction underlineAction; 
	protected StrikeThroughAction strikeThroughAction;
	
	protected SubscriptAction subscriptAction;
	protected SuperscriptAction superscriptAction;
	
	protected FontFaceAction fontFaceAction;
	protected FontSizeAction fontSizeAction;
	protected FontColorAction fontColorAction;
	
	protected AlignmentAction leftAlignAction;
	protected AlignmentAction centerAlignAction;
	protected AlignmentAction rightAlignAction;
	protected AlignmentAction justifiedAlignAction;
	 
	protected ButtonStyled boldButton;
	protected ButtonStyled italicButton;
	protected ButtonStyled underlineButton;
	protected ButtonStyled strikeThroughButton;
	protected ButtonStyled subscriptButton;
	protected ButtonStyled superscriptButton;
	
	protected ViewCombo<String> fontFaceCombo;
	protected ViewCombo<String> fontSizeCombo;
	
	protected ButtonStyled fontColorButton;
	
	protected ButtonStyled leftAlignButton;
	protected ButtonStyled centerAlignButton;
	protected ButtonStyled rightAlignButton;
	protected ButtonStyled justifiedAlignButton;

	protected boolean bold;
	protected boolean italic;
	protected boolean underline;
	protected boolean deleteline;
	protected Font targetFont;
	protected Color targetColor;
	 
	protected LinkedHashMap<String, Double> fontSizeMap;
	
	protected EditTextUserName titlePane;
	protected TextPaneHTMLEditor editorPane;
	protected ButtonStyled sendButton;

	public Scene_NoticeEditor(){
		super(SceneController.SCENE_NOTICE_EDITOR, null, null);
	}
	
	public Scene_NoticeEditor(String key){
		super(SceneController.SCENE_NOTICE_EDITOR, null, key);
	}

	public Scene_NoticeEditor(String key, String username, String tabKey){
		super(key, username, tabKey);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.setLayout(new BorderLayout());
		this.fontSizeMap = LanguageModel.getFontSizeMap();
		this.targetColor = GraphicsController.DEFAULT_TEXT_COLOR;
		this.targetFont = LanguageModel.getDefaultFont();
		
		this.undoHandler = new UndoHandler(); 
		this.undoManager = new UndoManager(); 
		this.undoAction = new UndoAction(); 
		this.redoAction = new RedoAction(); 

		this.boldAction = new StyledEditorKit.BoldAction(); 
		this.italicAction = new StyledEditorKit.ItalicAction(); 
		this.underlineAction = new StyledEditorKit.UnderlineAction();
		this.strikeThroughAction = new StrikeThroughAction();
		
		this.subscriptAction = new SubscriptAction();
		this.superscriptAction = new SuperscriptAction();
		
		this.fontFaceAction = new FontFaceAction(LanguageModel.DEFAULT_FONT);
		this.fontSizeAction = new FontSizeAction(LanguageModel.DEFAULT_FONTSIZE);
		this.fontColorAction = new FontColorAction(this.targetColor);
		
		this.leftAlignAction = new AlignmentAction("ALIGN_LEFT",StyleConstants.ALIGN_LEFT); 
		this.centerAlignAction = new AlignmentAction("ALIGN_CENTER",StyleConstants.ALIGN_CENTER); 
		this.rightAlignAction = new AlignmentAction("ALIGN_RIGHT",StyleConstants.ALIGN_RIGHT); 
		this.justifiedAlignAction = new AlignmentAction("ALIGN_JUSTIFIED",StyleConstants.ALIGN_JUSTIFIED); 
	}
	
	@Override
	protected void create(){
		super.create();
		this.createContent();
		this.createNewDocument();	
	}
	
	protected void createContent() {
		this.createToolBar();
		this.createTextPane();
	}
	
	protected void createToolBar() {
		ViewStyled toolBar = new ViewStyled() {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(Color.GRAY);
				graphics.fillRect(0, this.getHeight() - 1, this.getWidth(), 1);
			}
		};
		toolBar.setPreferredSize(new Dimension(0, 130));
		toolBar.setBackground(GraphicsController.EMPTY_COLOR);
		this.add(toolBar, BorderLayout.NORTH);
		
		this.createTitle(toolBar);
		this.createFile(toolBar, 5  , 50);
		this.createType(toolBar, 90 , 50);
		this.createPage(toolBar, 420, 50);
		this.createSend(toolBar, 515, 95);
	}
	
	protected void createTitle(ViewStyled toolBar) {
		this.titlePane = new EditTextUserName("UI->Scene->NoticeEditor::Title", Color.LIGHT_GRAY, Color.BLACK);
		this.titlePane.setBackground(GraphicsController.EMPTY_COLOR);
		this.titlePane.setBounds(0, 0, 300, 50);
		toolBar.add(this.titlePane);
	}
	
	protected void createSend(ViewStyled toolBar, int sendX, int sendY) {
		int placeX = sendX;
		int placeY = sendY;
		
		TextView module = new TextView("UI->Scene->NoticeEditor::Send");
		module.setBounds(placeX - 3, placeY + 2, 100, 20);
		module.setFont(LanguageModel.getDefaultFont(18));
		toolBar.add(module);
		this.sendButton = new ButtonStyled(null, GraphicsController.HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.TOUCH_COLOR) {
			@Override
			protected void onMouseClicked(MouseEvent e) {
				super.onMouseClicked(e);
				onSendClicked(e);
			}
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(GraphicsController.DEFAULT_TEXT_COLOR);
				graphics.fillRect(this.getWidth()/2 - 1, this.getHeight()/3, 3, this.getHeight()/3);
				graphics.fillRect(this.getWidth()/3, this.getHeight()/2 - 1, this.getWidth()/3, 3);
		    }
		};
		this.sendButton.setBounds(placeX + 7, placeY - 40, 35, 35);
		this.sendButton.setShade(5);
		this.sendButton.setRadius(20);
		toolBar.add(this.sendButton);
	}
	
	protected void onSendClicked(MouseEvent e) {
		this.getSceneController().removeScene(this.getKey());
	}

	protected void createFile(ViewStyled toolBar, int fileX, int fileY) {
		int placeX = fileX; 
		int placeY = fileY;
		ButtonStyled importButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void onMouseClicked(MouseEvent e) {
				super.onMouseClicked(e);
				JFileChooser fileChooser = new JFileChooser("./");
				fileChooser.setDialogTitle(LanguageModel.get("UI->Scene->NoticeEditor::ChooseHTML"));
    			FileFilter filter = new FileNameExtensionFilter(LanguageModel.get("UI->Scene->NoticeEditor::HTMLFliter"), "htm", "html");
    			fileChooser.addChoosableFileFilter(filter);
    			fileChooser.setAcceptAllFileFilterUsed(false);
    			fileChooser.setApproveButtonText(LanguageModel.get("UI->Scene->NoticeEditor::Approve"));
				if(fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
					editorPane.setFile(fileChooser.getSelectedFile().getPath());
					editorPane.refresh();
				}
			}
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(Color.BLACK);
				graphics.fillRect(17, 5, 2, 8);
				graphics.fillRect(14, 8, 8, 2);
				graphics.setFont(LanguageModel.getDefaultFont(12));
				graphics.drawString(".html", 3, 30);
		    }
		};
		importButton.setBounds(placeX, placeY, 35, 35);
		importButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->ImportHTML");
		toolBar.add(importButton);
		
		placeX += 40;
		ButtonStyled imageButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void onMouseClicked(MouseEvent e) {
				super.onMouseClicked(e);
				JFileChooser fileChooser = new JFileChooser("./");
				fileChooser.setDialogTitle(LanguageModel.get("UI->Scene->NoticeEditor::ChoosePicture"));
    			FileFilter filter = new FileNameExtensionFilter(LanguageModel.get("UI->Scene->NoticeEditor::PictureFliter"), "jpg", "png");
    			fileChooser.addChoosableFileFilter(filter);
    			fileChooser.setAcceptAllFileFilterUsed(false);
    			fileChooser.setApproveButtonText(LanguageModel.get("UI->Scene->NoticeEditor::Approve"));
				if(fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION) {
					ImageIcon image = new ImageIcon(fileChooser.getSelectedFile().getPath());
    				double percent = Math.min(((double)editorPane.getWidth() - 60) / image.getIconWidth() ,1);
    				editorPane.insertIcon(new ImageIcon(image.getImage().getScaledInstance((int)(percent * image.getIconWidth()), (int)(percent * image.getIconHeight()), 0)));
					editorPane.refresh();
				}
			}
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.defaultColor);
				graphics.fillRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
				graphics.setColor(Color.BLACK);
				graphics.drawRect(6, 6, this.getWidth() - 12, this.getHeight() - 12);
				graphics.setColor(GraphicsController.HINT_BLUE_COLOR);
				graphics.fillPolygon(new int[]{8, 8, 27, 27, 24, 19, 12}, new int[]{17, 27, 27, 20, 16, 20, 13}, 7);
				graphics.setColor(new Color(250, 230, 100));
				graphics.fillOval(18, 9, 5, 5);
		    }
		};
		imageButton.setBounds(placeX, placeY, 35, 35);
		imageButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->InsertPicture");
		toolBar.add(imageButton);
		
		placeX = fileX; 
		placeY += 40;
		ButtonStyled undoButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(undoManager.canUndo() ? GraphicsController.HINT_BLUE_COLOR : GraphicsController.TOUCH_COLOR);
				int targetX = 5;
				int targetY = getHeight()/2;
				graphics.fillPolygon(new int[]{targetX, targetX + 8, targetX + 8, targetX + 24, targetX + 24, targetX + 8, targetX + 8},
						new int[]{targetY, targetY + 10, targetY + 6, targetY + 6, targetY - 6, targetY - 6, targetY - 10}, 7);
		    }
			@Override
			public void update() {
				super.update();
				this.setActive(undoManager.canUndo());
			}
		};
		undoButton.setBounds(placeX, placeY, 35, 35);
		undoButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->Undo");
		undoButton.setAction(this.undoAction);
		undoButton.refresh();
		toolBar.add(undoButton);
		
		placeX += 40;
		ButtonStyled redoButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(undoManager.canRedo() ? GraphicsController.HINT_BLUE_COLOR : GraphicsController.TOUCH_COLOR);
				int targetX = 30;
				int targetY = getHeight()/2;
				graphics.fillPolygon(new int[]{targetX, targetX - 8, targetX - 8, targetX - 24, targetX - 24, targetX - 8, targetX - 8},
						new int[]{targetY, targetY + 10, targetY + 6, targetY + 6, targetY - 6, targetY - 6, targetY - 10}, 7);
		    }
			@Override
			public void update() {
				super.update();
				this.setActive(undoManager.canRedo());
			}
		};
		redoButton.setBounds(placeX, placeY, 35, 35);
		redoButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->Redo");
		redoButton.setAction(this.redoAction);
		redoButton.refresh();
		toolBar.add(redoButton);
		
		placeX += 40;
		placeY -= 40;
		this.addCutOff(toolBar, placeX, placeY);
	}
	
	protected void createType(ViewStyled toolBar, int typeX, int typeY) {
		int placeX = typeX; 
		int placeY = typeY + 40;
		this.boldButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.setFont(LanguageModel.getDefaultFont(Font.BOLD, 18));
				graphics.drawString("B", (this.getWidth() - 12)/ 2, (this.getHeight() + 16)/2);
		    }
			@Override
			protected boolean lockEvent() {
				return StyleConstants.isBold(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes());
			}
		};
		this.boldButton.setBounds(placeX, placeY, 35, 35);
		this.boldButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->Bold");
		this.boldButton.setAction(this.boldAction);
		this.boldButton.refresh();
		toolBar.add(this.boldButton);
		
		placeX += 40;
		this.italicButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.setFont(new Font("MS Reference Sans Serif", Font.ITALIC, 19));
				graphics.drawString("I", (this.getWidth() - 8)/ 2, (this.getHeight() + 16)/2);
		    }
			@Override
			protected boolean lockEvent() {
				return StyleConstants.isItalic(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes());
			}
		};
		this.italicButton.setBounds(placeX, placeY, 35, 35);
		this.italicButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->Italic");
		this.italicButton.setAction(this.italicAction);
		this.italicButton.refresh();
		toolBar.add(this.italicButton);
		
		placeX += 40;
		this.underlineButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.setFont(LanguageModel.getDefaultFont(17));
				graphics.drawString("U", (this.getWidth() - 12)/ 2, (this.getHeight() + 14)/2);
				graphics.fillRect(this.getWidth()/3 + 1, (this.getHeight() + 16)/2, this.getWidth()/3, 1);
		    }
			@Override
			protected boolean lockEvent() {
				return StyleConstants.isUnderline(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes());
			}
		};
		this.underlineButton.setBounds(placeX, placeY, 35, 35);
		this.underlineButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->Underline");
		this.underlineButton.setAction(this.underlineAction);
		this.underlineButton.refresh();
		toolBar.add(this.underlineButton);
		
		placeX += 40;
		this.strikeThroughButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.setFont(LanguageModel.getDefaultFont(Font.PLAIN, 16));
				graphics.drawString("abc", (this.getWidth() - 26)/ 2, (this.getHeight() + 14)/2);
				graphics.fillRect(this.getWidth()/8, this.getHeight()/2 + 3, this.getWidth()*3/4 + 1, 1);
		    }
			@Override
			protected boolean lockEvent() {
				return StyleConstants.isStrikeThrough(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes());
			}
		};
		this.strikeThroughButton.setBounds(placeX, placeY, 35, 35);
		this.strikeThroughButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->StrikeThrough");
		this.strikeThroughButton.setAction(this.strikeThroughAction);
		this.strikeThroughButton.refresh();
		toolBar.add(this.strikeThroughButton);
		
		placeX += 40;
		this.subscriptButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.setFont(LanguageModel.getDefaultFont(Font.BOLD, 18));
				graphics.drawString("x", (this.getWidth() - 14)/ 2, (this.getHeight() + 14)/2);
				graphics.setColor(GraphicsController.HINT_BLUE_COLOR);
				graphics.setFont(LanguageModel.getDefaultFont(Font.BOLD, 9));
				graphics.drawString("2", (this.getWidth() + 8)/ 2, (this.getHeight() + 18)/2);
		    }
			@Override
			protected boolean lockEvent() {
				return StyleConstants.isSubscript(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes());
			}
		};
		this.subscriptButton.setBounds(placeX, placeY, 35, 35);
		this.subscriptButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->Subscript");
		this.subscriptButton.setAction(this.subscriptAction);
		this.subscriptButton.refresh();
		toolBar.add(this.subscriptButton);
		
		placeX += 40;
		this.superscriptButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.setFont(LanguageModel.getDefaultFont(Font.BOLD, 18));
				graphics.drawString("x", (this.getWidth() - 14)/ 2, (this.getHeight() + 14)/2);
				graphics.setColor(GraphicsController.HINT_BLUE_COLOR);
				graphics.setFont(LanguageModel.getDefaultFont(Font.BOLD, 9));
				graphics.drawString("2", (this.getWidth() + 8)/ 2, (this.getHeight() - 2)/2);
		    }
			@Override
			protected boolean lockEvent() {
				return StyleConstants.isSuperscript(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes());
			}
		};
		this.superscriptButton.setBounds(placeX, placeY, 35, 35);
		this.superscriptButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->Superscript");
		this.superscriptButton.setAction(this.superscriptAction);
		this.superscriptButton.refresh();
		toolBar.add(this.superscriptButton);
		
		placeX = typeX + 5;
		placeY -= 40;
		this.fontFaceCombo = new ViewCombo<String>() {
			@Override
			protected void setButton(ButtonStyled button, String item) {
				super.setButton(button, item);
				button.setFont(new Font(item.toString(), Font.PLAIN, this.chooseFontSize));
			}
			@Override
			protected void onSelectItem(String item) {
				super.onSelectItem(item);
				setFontFace(item, false);
				fontFaceAction.fontFaceAction(editorPane, item);
			}
			@Override
			public void update() {
				super.update();
				this.select(StyleConstants.getFontFamily(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes()), false);
			}
		};
		this.fontFaceCombo.setBounds(placeX, placeY + 3, 155, 29);
		this.fontFaceCombo.setChooseFontSize(18);
		this.fontFaceCombo.setPadding(4);
		this.fontFaceCombo.setSet(LanguageModel.getFontList());
		this.fontFaceCombo.setFont(LanguageModel.getDefaultFont(16));
		toolBar.add(this.fontFaceCombo);
		
		placeX += 160;
		this.fontSizeCombo = new ViewCombo<String>() {
			@Override
			protected void onSelectItem(String item) {
				super.onSelectItem(item);
				setFontSize(fontSizeMap.get(item).intValue(), false);
				fontSizeAction.fontSizeAction(editorPane, fontSizeMap.get(item).intValue());
			}
			@Override
			protected void onSelectOther(String item) {
				super.onSelectOther(item);
				setFontSize(fontSizeMap.get(item).intValue(), false);
				fontSizeAction.fontSizeAction(editorPane, fontSizeMap.get(item).intValue());
			}
			@Override
			public void update() {
				super.update();
				this.select(Integer.toString(StyleConstants.getFontSize(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes())), false);
			}
		};
		this.fontSizeCombo.setBounds(placeX, placeY + 3, 75, 29);
		this.fontSizeCombo.setChooseFontSize(16);
		this.fontSizeCombo.setPadding(4);
		this.fontSizeCombo.setSet(this.fontSizeMap.keySet().toArray(new String[]{}));
		this.fontSizeCombo.setFont(LanguageModel.getDefaultFont(16));
		toolBar.add(this.fontSizeCombo);
		
		placeX += 80;
		ButtonStyled addSizeButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.setFont(LanguageModel.getDefaultFont(Font.PLAIN, 18));
				graphics.drawString("A", (this.getWidth() - 14)/ 2, (this.getHeight() + 14)/2);
				graphics.setColor(GraphicsController.HINT_BLUE_COLOR);
				int targetX = 23;
				int targetY = 8;
				graphics.fillPolygon(new int[]{targetX, targetX - 4, targetX + 4}, new int[]{targetY - 2, targetY + 2, targetY + 2}, 3);
		    }
		};
		addSizeButton.setBounds(placeX, placeY, 35, 35);
		addSizeButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->AddSize");
		addSizeButton.addActionListener(e ->{
			setFontSize(this.targetFont.getSize() + 1, false);
			fontSizeAction.fontSizeAction(editorPane, this.targetFont.getSize() + 1);
		});
		toolBar.add(addSizeButton);
		
		placeX += 40;
		ButtonStyled minusSizeButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.setFont(LanguageModel.getDefaultFont(Font.PLAIN, 18));
				graphics.drawString("A", (this.getWidth() - 14)/ 2, (this.getHeight() + 16)/2);
				graphics.setColor(GraphicsController.HINT_BLUE_COLOR);
				int targetX = 23;
				int targetY = 10;
				graphics.fillPolygon(new int[]{targetX, targetX - 4, targetX + 4}, new int[]{targetY + 2, targetY - 2, targetY - 2}, 3);
		    }
		};
		minusSizeButton.setBounds(placeX, placeY, 35, 35);
		minusSizeButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->MinusSize");
		minusSizeButton.addActionListener(e ->{
			setFontSize(this.targetFont.getSize() - 1, false);
			fontSizeAction.fontSizeAction(editorPane, this.targetFont.getSize() - 1);
		});
		toolBar.add(minusSizeButton);
		
		placeX -= 35;
		placeY += 40;
		this.fontColorButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.setFont(LanguageModel.getDefaultFont(Font.PLAIN, 18));
				graphics.drawString("A", (this.getWidth() - 12)/ 2, (this.getHeight() + 8)/2);
				graphics.setColor(targetColor);
				graphics.fillRect(this.getWidth()/6 + 1, this.getHeight() - 11, this.getWidth()*2/3, 5);
		    }
		};
		this.fontColorButton.setBounds(placeX, placeY, 35, 35);
		this.fontColorButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->FontColor");
		this.fontColorButton.setAction(this.fontColorAction);
		this.fontColorButton.refresh();
		toolBar.add(this.fontColorButton);
		
		ButtonStyled colorChooseButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void onMouseClicked(MouseEvent e) {
				super.onMouseClicked(e);
				Color returnColor = JColorChooser.showDialog(Scene_NoticeEditor.this, "颜色选择器", targetColor);
				if(returnColor != null) {
					setFontColor(returnColor);
					fontColorAction.fontColorAction(editorPane, targetColor);
				}
			}
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.fillPolygon(new int[]{10, 6, 14}, new int[]{20, 16, 16}, 3);
		    }
		};
		colorChooseButton.setBounds(placeX + 35, placeY, 20, 35);
		colorChooseButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->FontColor");
		toolBar.add(colorChooseButton);
		
		placeX += 75;
		placeY -= 40;
		this.addCutOff(toolBar, placeX, placeY);
		
	}
	
	protected void createPage(ViewStyled toolBar, int pageX, int pageY) {
		int placeX = pageX;
		int placeY = pageY;
		this.leftAlignButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.fillRect(this.getWidth()/6 + 1, 11, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 1, 15, this.getWidth()*2/3 - 4, 1);
				graphics.fillRect(this.getWidth()/6 + 1, 19, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 1, 23, this.getWidth()*2/3 - 4, 1);
		    }
//			@Override
//			protected boolean lockEvent() {
//				return StyleConstants.getAlignment(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes()) == StyleConstants.ALIGN_LEFT;
//			}
		};
		this.leftAlignButton.setBounds(placeX, placeY, 35, 35);
		this.leftAlignButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->LeftAlign");
		this.leftAlignButton.setAction(this.leftAlignAction);
		this.leftAlignButton.refresh();
		toolBar.add(this.leftAlignButton);
		
		placeX += 40;
		this.rightAlignButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.fillRect(this.getWidth()/6 + 1, 11, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 5, 15, this.getWidth()*2/3 - 4, 1);
				graphics.fillRect(this.getWidth()/6 + 1, 19, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 5, 23, this.getWidth()*2/3 - 4, 1);
		    }
//			@Override
//			protected boolean lockEvent() {
//				return StyleConstants.getAlignment(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes()) == StyleConstants.ALIGN_RIGHT;
//			}
		};
		this.rightAlignButton.setBounds(placeX, placeY, 35, 35);
		this.rightAlignButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->RightAlign");
		this.rightAlignButton.setAction(this.rightAlignAction);
		this.rightAlignButton.refresh();
		toolBar.add(this.rightAlignButton);
		
		placeX = pageX;
		placeY += 40;
		this.centerAlignButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.fillRect(this.getWidth()/6 + 1, 11, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 4, 15, this.getWidth()*2/3 - 6, 1);
				graphics.fillRect(this.getWidth()/6 + 1, 19, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 4, 23, this.getWidth()*2/3 - 6, 1);
		    }
//			@Override
//			protected boolean lockEvent() {
//				return StyleConstants.getAlignment(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes()) == StyleConstants.ALIGN_CENTER;
//			}
		};
		this.centerAlignButton.setBounds(placeX, placeY, 35, 35);
		this.centerAlignButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->CenterAlign");
		this.centerAlignButton.setAction(this.centerAlignAction);
		this.centerAlignButton.refresh();
		toolBar.add(this.centerAlignButton);
		
		placeX += 40;
		this.justifiedAlignButton = new ButtonStyled(null, GraphicsController.DARK_HOVER_COLOR, GraphicsController.DEFAULT_COLOR, GraphicsController.DARK_TOUCH_COLOR) {
			@Override
			protected void paint(Graphics2D graphics) {
				super.paint(graphics);
				graphics.setColor(this.textColor);
				graphics.fillRect(this.getWidth()/6 + 1, 9, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 1, 13, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 1, 17, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 1, 21, this.getWidth()*2/3, 1);
				graphics.fillRect(this.getWidth()/6 + 1, 25, this.getWidth()*2/3, 1);
		    }
//			@Override
//			protected boolean lockEvent() {
//				return StyleConstants.getAlignment(((StyledEditorKit)editorPane.getEditorKit()).getInputAttributes()) == StyleConstants.ALIGN_JUSTIFIED;
//			}
		};
		this.justifiedAlignButton.setBounds(placeX, placeY, 35, 35);
		this.justifiedAlignButton.setToolTipKey("UI->Window->NoticeEditor::ToolTip->Justified");
		this.justifiedAlignButton.setAction(this.justifiedAlignAction);
		this.justifiedAlignButton.refresh();
		toolBar.add(this.justifiedAlignButton);
		
		placeX += 40;
		placeY -= 40;
		this.addCutOff(toolBar, placeX, placeY);
	}	

	protected void createTextPane() {
		this.editorPane = new TextPaneHTMLEditor();
		this.editorPane.setBackground(GraphicsController.EMPTY_COLOR);
		this.editorPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		this.add(new ScrollPane(this.editorPane), BorderLayout.CENTER);
	}
	
	public void createNewDocument(){ 
		this.setBold(false);
		this.setItalic(false);
		this.setUnderline(false);
		this.setDeleteline(false);
		this.setFontColor(this.targetColor);
		this.setFontFace(this.targetFont.getFamily(), true);
		this.setFontSize(this.targetFont.getSize(), true);
		this.fontFaceAction.fontFaceAction(editorPane, this.targetFont.getFamily());
		this.fontSizeAction.fontSizeAction(editorPane, this.targetFont.getSize());
		this.editorPane.setFont(this.targetFont);
		this.editorPane.setForeground(this.targetColor);
		
		Document document = this.editorPane.getDocument(); 
		if(document != null) document.removeUndoableEditListener(undoHandler);
		this.editorPane.setDocument(this.editorPane.getEditorKit().createDefaultDocument()); 
		this.editorPane.getDocument().addUndoableEditListener(undoHandler); 
		this.resetUndoManager(); 
	}
	
	protected void addCutOff(ViewStyled toolBar, int placeX, int placeY) {
		ViewStyled cutOffStyled = new ViewStyled();
		cutOffStyled.setBackground(GraphicsController.TOUCH_COLOR);
		cutOffStyled.setBounds(placeX, placeY + 3, 1, 69);
		toolBar.add(cutOffStyled);
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		this.titlePane.setBounds(0, 0, width, this.titlePane.getHeight());
	}
	
	@Override
	public void refresh() {
		super.refresh();
		this.fontSizeMap = LanguageModel.getFontSizeMap();
		this.fontSizeCombo.setSet(this.fontSizeMap.keySet().toArray(new String[0]));
		if(this.getParent() != null) {
			this.fontFaceCombo.setOwner(this.getSceneController().getWindow());
			this.fontSizeCombo.setOwner(this.getSceneController().getWindow());
		}
	}
	
	protected void setBold(boolean value) {
		this.bold = value;
		this.boldButton.setLocked(value);
	}
	protected void setItalic(boolean value) {
		this.italic = value;
		this.boldButton.setLocked(value);
	}
	protected void setUnderline(boolean value) {
		this.underline = value;
		this.boldButton.setLocked(value);
	}
	protected void setDeleteline(boolean value) {
		this.deleteline = value;
		this.boldButton.setLocked(value);
	}
	protected void setFontColor(Color color) {
		this.targetColor = color;
		this.fontColorAction.setColor(this.targetColor);
		this.fontColorButton.repaint();
	}
	protected void setFontFace(String fontFace, boolean select) {
		this.targetFont = new Font(fontFace, Font.PLAIN, this.targetFont.getSize());
		if(select) this.fontFaceCombo.select(fontFace, false);
	}
	protected void setFontSize(int fontSize, boolean select) {
		this.targetFont = new Font(this.targetFont.getFamily(), Font.PLAIN, fontSize);
		if(select)this.fontSizeCombo.select(Integer.toString(fontSize), false);
	}	

	protected void resetUndoManager() { 
		undoManager.discardAllEdits(); 
		undoAction.setEnabled(undoManager.canUndo()); 
		redoAction.setEnabled(undoManager.canRedo()); 
	} 
	
	/**
	 * 内部类
	 */
	class UndoHandler implements UndoableEditListener { 
		@Override
		public void undoableEditHappened(UndoableEditEvent e) {
			undoManager.addEdit(e.getEdit()); 
			undoAction.setEnabled(undoManager.canUndo()); 
			redoAction.setEnabled(undoManager.canRedo()); 
		}
	}

	class UndoAction extends AbstractAction { 
		public UndoAction() { 
			super("Undo");
			this.setEnabled(false); 
		} 
		@Override
		public void actionPerformed(ActionEvent e) { 
			try { 
				undoManager.undo(); 
			} catch (CannotUndoException exception) { 
				exception.printStackTrace(); 
			}
			this.setEnabled(undoManager.canUndo()); 
			redoAction.setEnabled(undoManager.canRedo());  
		} 
	} 

	class RedoAction extends AbstractAction { 
		public RedoAction() {
			super("Redo");
			this.setEnabled(false);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.redo(); 
			}catch (CannotRedoException exception){ 
				exception.printStackTrace(); 
			}
			this.setEnabled(undoManager.canRedo()); 
			undoAction.setEnabled(undoManager.canUndo());  
		}
	} 
	
}
