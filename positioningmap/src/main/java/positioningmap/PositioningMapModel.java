package positioningmap;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import positioningmap.Main.Better;
import positioningmap.Main.SpecTypeEnum;

interface PositioningMapModelListener {

	void onUpdate();
	
	
};
public class PositioningMapModel {
	private static final int LABEL_COUNT = 9;
	private List<PositioningMapElement> elements = new ArrayList<>();
	private int width;
	private int height;
	private double xoffset;
	private double xratio;
	private double yoffset;
	private double yratio;
	private double xspan;
	private double yspan;
	private List<String> xlabels = new ArrayList<>();
	private List<String> ylabels = new ArrayList<>();
	private double marginRatio = 0.0;
	private String xAxisTitle;
	private SpecSheet specSheet;
	private String xAxisCategory;
	private String xAxisSpec;
	private String yAxisCategory;
	private String yAxisSpec;
	private String yAxisTitle;
	private UseCaseContainer pmdefs;
	
	public PositioningMapModel(SpecSheet specSheet, UseCaseContainer pmdefs2) {
		this.specSheet = specSheet;
		this.pmdefs = pmdefs2;
		
		specSheet.getCategories().forEach((k,v) -> {
			v.getSpecs().forEach((kk, vv) -> {
				xAxisCategory = k;
				xAxisSpec = kk;
				yAxisCategory = k;
				yAxisSpec = kk;
				return;
			});
		});
		
	}
	
	private NumberFormat formatter = new DecimalFormat("#0.00");
	private PositioningMapModelListener listener; 
	private void createData() {

		xlabels.clear();
		ylabels.clear();
		elements.clear();
		
		xAxisTitle = xAxisCategory + " / " + xAxisSpec + " [" + this.specSheet.getSpecs(xAxisCategory).get(xAxisSpec).getUnit() + "]";
//		yAxisTitle = yAxisCategory + " / " + yAxisSpec + " [" + this.specSheet.getSpecs(yAxisCategory).get(yAxisSpec).getUnit() + "]";
		
		SpecDef specDefX = specSheet.getCategories().get(xAxisCategory).getSpecs().get(xAxisSpec);
//		SpecDef specDefY = specSheet.getCategories().get(yAxisCategory).getSpecs().get(yAxisSpec);
		
		UseCaseDef usecaseDef = pmdefs.get(this.yAxisSpec);
		List<String> targetIds = usecaseDef.getDefines();
		List<String> targetSPecNames = new ArrayList<>();
		List<SpecDef> targetSpecs = new ArrayList<>();
		targetIds.forEach(t -> {
			SpecDef spec = specSheet.find(t);
			targetSpecs.add(spec);
			targetSPecNames.add(spec.getName()); 
		});
		yAxisTitle = targetSPecNames.toString();

		ScoreCalculator calc = new ScoreCalculator(specSheet, targetSpecs, usecaseDef);
		
		double xmaxMax = Double.NEGATIVE_INFINITY;
		double xminMin = Double.POSITIVE_INFINITY;
		double ymaxMax = Double.NEGATIVE_INFINITY;
		double yminMin = Double.POSITIVE_INFINITY;
		int j = 0;
		for (String product : specSheet.products().keySet()) {
//			if (j++ > 5) {
//				break;
//			}
			Map<String, SpecHolder> values = specSheet.getProductSpecs().get(product).getValues();
			
			SpecHolder specX = values.get(specDefX.getId());
			if (specX == null) {
				continue;
			}
			SpecValue specValueX = getValue(specX);

			double xmin = specValueX.getX();
			double xmax = specValueX.getY();
			xmaxMax = Math.max(Math.max(xmax, xmaxMax), xmin);
			xminMin = Math.min(Math.min(xmin, xminMin), xmax);	

			CalcResult result = calc.calc(product);
			if (result == null) {
				continue;
			}
//			SpecValue specValueY = getValue(specY);
			double ymin = 0.0;
			double ymax = 0.0;

			ymin = result.min;
			ymax = result.max;
			
			ymaxMax = Math.max(Math.max(ymax, ymaxMax), ymin);
			yminMin = Math.min(Math.min(ymin, yminMin), ymax);
			
			
			MyColor color = null;
			if (product.contains("Viavi")) {
				color = new MyColor(128, 0, 128);
			}
			else if (product.contains("EXFO")) {
				color = new MyColor(0, 123, 195);
			}
			else if (product.contains("Anritsu")) {
				color = new MyColor(0, 155, 119);
			}
			PositioningMapElement e = new PositioningMapElement(product, xmin, xmax, ymin, ymax, color) {

				@Override
				protected int calcX(double x) {
					int ret = (int)(width * marginRatio + (x - xoffset) * xratio);
					return ret;
				}

				@Override
				protected int calcY(double y) {
					int ret = height - (int)(height * marginRatio + (y - yoffset) * yratio);
					return ret;
				}

				@Override
				protected int calcWidht(double d) {
					return (int)((double)width * d / xspan);
				}

				@Override
				protected int calcHeight(double d) {
					if (d == 0.0) {
						return height / 100;
					}
					return (int)((double)height * d / yspan);
				}
				
			};
			elements.add(e);
		}

		double m = 0.0;
		xmaxMax = xmaxMax + (xmaxMax - xminMin) * m;
		xminMin = xminMin - (xmaxMax - xminMin) * m;
		
		ymaxMax = calc.minMax().max;
		yminMin = calc.minMax().min;
		ymaxMax = ymaxMax + (ymaxMax - yminMin) * m;
		yminMin = yminMin - (ymaxMax - yminMin) * m;		
		
		xspan = (xmaxMax - xminMin);
		yspan = (ymaxMax - yminMin);
		
		this.updateRatio();
		
		xoffset = xminMin;//xspan * marginRatio;
		yoffset = yminMin;
		
		for (int i = 0; i < LABEL_COUNT; i++) {
			double x = xminMin + xspan/ (double)(LABEL_COUNT-1) * i;
			xlabels.add(formatter.format(x));
			
			double y = yminMin + yspan/ (double)(LABEL_COUNT-1) * (LABEL_COUNT-i-1);
			ylabels.add(formatter.format(y));
		}
		
		if (listener != null) {
			listener.onUpdate();
		}
	}

	private SpecValue getValue(SpecHolder specX) {
		SpecValue ret = specX.getGuarantee();
		if (!ret.getDefined()) {
			ret = specX.getTypical();
		}
		return ret;
	}

	public List<PositioningMapElement> elements() {
		return elements;
	}

	public void setSize(int width2, int height2) {
		this.width = width2;
		this.height = height2;
		
		updateRatio();
	}

	private void updateRatio() {
		xratio = (this.width * (1.0 - marginRatio * 2.0)) / xspan;
		yratio = (this.height * (1.0 - marginRatio * 2.0)) / yspan;
	}

	public int xLabelCount() {
		return xlabels.size();
	}

	public String xLabel(int i) {
		return xlabels.get(i);
	}

	public int yLabelCount() {
		return ylabels.size();
	}
	
	public String yLabel(int i) {
		return ylabels.get(i);
	}
	
	public double marginRatio() {
		return marginRatio;
	}

	public String xAxisTitle() {
		return xAxisTitle;
	}

	public String yAxisTitle() {
		return yAxisTitle;
	}
	
	public List<String> getSpecList() {
		List<String> ret = new ArrayList<>();
		this.specSheet.getCategories().forEach((k, v)->{
			v.getSpecs().forEach((kk, vv) -> {
				ret.add(k + "|" + kk);
			});
		});
		return ret;
	}

	public void setX(String category, String specname) {
		xAxisCategory = category;
		xAxisSpec = specname;
		createData();
	}

	public void setY(String category, String specname) {
		yAxisCategory = category;
		yAxisSpec = specname;
		createData();
	}

	public List<String> getUseCases() {
		return this.pmdefs.defs();
	}

	public void setUseCase(String specname) {
		this.yAxisSpec = specname;
		createData();
	}

	public void update() {
		this.createData();
	}

	public void setListener(PositioningMapModelListener positioningMapModelListener) {
		this.listener = positioningMapModelListener;
	}
}
