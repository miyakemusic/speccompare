package positioningmap;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PositioningMapUi extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private PositioningMapModel positioningMapModel;
	protected Component canvas;


	public PositioningMapUi(PositioningMapModel positioningMapModel) {
		this.setSize(new Dimension(1000, 800));
		this.getContentPane().setLayout(new BorderLayout());
		
		positioningMapModel.setListener(new PositioningMapModelListener() {
			@Override
			public void onUpdate() {
				canvas.repaint();
			}
		});
		
		this.getContentPane().add(canvas = new MyCanvas(), BorderLayout.CENTER);
		
		this.positioningMapModel = positioningMapModel;
		
		positioningMapModel.setSize(canvas.getWidth(), canvas.getHeight());
		
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				positioningMapModel.setSize(canvas.getWidth(), canvas.getHeight());
			}
		});
		
		JPanel toolBar = new JPanel();
		this.getContentPane().add(toolBar, BorderLayout.NORTH);
		toolBar.setLayout(new FlowLayout());
		toolBar.add(createCombo("X", positioningMapModel.getSpecList(), new MyComboListener() {
			@Override
			public void onChange(String category, String specname) {
				positioningMapModel.setX(category, specname);
			//	canvas.repaint();
			}
		}));
		
//		toolBar.add(createCombo("Y", positioningMapModel.getSpecList(), new MyComboListener() {
//			@Override
//			public void onChange(String category, String specname) {
//				positioningMapModel.setY(category, specname);
//			//	canvas.repaint();
//			}
//		}));
		
		toolBar.add(createCombo("Y", positioningMapModel.getUseCases(), new MyComboListener() {
			@Override
			public void onChange(String category, String specname) {
				positioningMapModel.setUseCase(specname);
			//	canvas.repaint();
			}
		}));
	}

	
	private Component createCombo(String title, List<String> positioningMapModel2, MyComboListener listener) {
		JPanel ret = new JPanel();
		ret.setLayout(new FlowLayout());
		ret.add(new JLabel(title));
		JComboBox<String> combo = new JComboBox<>();
		ret.add(combo);
		
		positioningMapModel2.forEach(v -> {
			combo.addItem(v);
		});
		
		combo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = combo.getSelectedItem().toString();
				if (value.contains("|")) {
					String[] tmp = value.split("\\|");
					listener.onChange(tmp[0], tmp[1]);
				}
				else {
					listener.onChange("", value);
				}
			}
		});
		return ret;
	}

	interface MyComboListener {

		void onChange(String string, String string2);
		
	}
	class MyCanvas extends Canvas {

		public MyCanvas() {
			super();
		}

		@Override
		public void update(Graphics g) {
			// TODO Auto-generated method stub
			super.update(g);
		}

		@Override
		public void paint(Graphics g1) {
			super.paint(g1);
			
			System.out.println("paint");
			Graphics2D g = (Graphics2D)g1;
			
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.setColor(Color.black);
//		    BasicStroke stroke = new BasicStroke(1.0f);
//		    g.setStroke(stroke);
		    
			g.drawLine(0, MyCanvas.this.getHeight() / 2, MyCanvas.this.getWidth(), MyCanvas.this.getHeight() / 2);
			g.drawLine(MyCanvas.this.getWidth() / 2, 0, MyCanvas.this.getWidth() / 2, MyCanvas.this.getHeight());
			
			// horizontal labels
			double xstep = ((MyCanvas.this.getWidth()*(1-positioningMapModel.marginRatio()*2)) / (positioningMapModel.xLabelCount() - 1));
			double xmargin = (MyCanvas.this.getWidth() * positioningMapModel.marginRatio());
			for (int i = 0; i < positioningMapModel.xLabelCount(); i++) {
				int xoffset = 0;

				String label = positioningMapModel.xLabel(i);
				if (i == 0 || i == positioningMapModel.xLabelCount()/2) {
					xoffset = 0;
				}
				else if (i == positioningMapModel.xLabelCount()-1) {
					xoffset = -g.getFontMetrics().stringWidth(label);
				}				
				else {
					xoffset = -g.getFontMetrics().stringWidth(label)/2;
				}
				int x = (int)(xmargin + (double)i * xstep );
				g.drawLine(x, MyCanvas.this.getHeight()/2 - 5, x, MyCanvas.this.getHeight()/2);
				g.drawString(label, x + xoffset, MyCanvas.this.getHeight() / 2 + this.getFont().getSize());
			}
			
			// vertical labels
			double ystep = ((MyCanvas.this.getHeight()*(1-positioningMapModel.marginRatio()*2)) / (positioningMapModel.yLabelCount() - 1));
			double ymargin = (MyCanvas.this.getHeight() * positioningMapModel.marginRatio());
			for (int i = 0; i < positioningMapModel.yLabelCount(); i++) {
				String label = positioningMapModel.yLabel(i);
				int yoffset = 0;
				if ( i == 0) {
					yoffset = this.getFont().getSize();
				}
				int y = (int)(ymargin + (double)i * ystep);
				g.drawLine(MyCanvas.this.getWidth()/2 - 5, y, MyCanvas.this.getWidth()/2, y);
				g.drawString(label, MyCanvas.this.getWidth()/2, y + yoffset);
			}
			
			Font boldFont = new Font(this.getFont().getFontName(), Font.BOLD, this.getFont().getSize());
			Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
			fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			Font boldUnderlinedFont = new Font(this.getFont().getFontName(), Font.PLAIN, this.getFont().getSize()).deriveFont(fontAttributes);
			
			g.setFont(boldFont);
			g.drawString(positioningMapModel.xAxisTitle(), 0, MyCanvas.this.getHeight()/2 - 5);
			String xlabel = positioningMapModel.yAxisTitle();
			g.drawString(xlabel, MyCanvas.this.getWidth()/2 - g.getFontMetrics().stringWidth(xlabel), getFont().getSize());
			g.setFont(this.getFont());
			
			List<Font> fonts = Arrays.asList(boldUnderlinedFont, getFont(), boldFont);
			
			for (PositioningMapElement e : positioningMapModel.elements()) {
				//g.setColor(Color.YELLOW);
				g.setColor(new Color(e.color().r, e.color().g, e.color().b, 100));
				int height = e.height();
				g.fillOval(e.x(), e.y(), e.width(), height);
				
				g.setColor(Color.BLACK);
				String[] text = e.text().split("\n");
				
				int yoffset = (int)( (double)e.height() / (double)text.length);
				int xoffset = 0;
//				g.setFont(boldFont);
				for (int i = 0; i < text.length; i++) {
					g.setFont(fonts.get(i));
					g.drawString(text[i], xoffset + e.x() + (e.width() - g.getFontMetrics().stringWidth(text[i])) /2, e.y() + yoffset + + this.getFont().getSize()* i);
				}
				g.setFont(getFont());
			}
		}
		
	}
}
