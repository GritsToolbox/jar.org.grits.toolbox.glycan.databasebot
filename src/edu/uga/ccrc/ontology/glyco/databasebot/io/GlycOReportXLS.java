package edu.uga.ccrc.ontology.glyco.databasebot.io;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.eurocarbdb.MolecularFramework.io.SugarImporterException;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarExporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.io.GlycoCT.SugarImporterGlycoCTCondensed;
import org.eurocarbdb.MolecularFramework.sugar.Sugar;
import org.eurocarbdb.MolecularFramework.util.visitor.GlycoVisitorException;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.GraphicOptions;
import org.eurocarbdb.application.glycanbuilder.Union;
import org.eurocarbdb.application.glycoworkbench.GlycanWorkspace;

import edu.uga.ccrc.ontology.glyco.databasebot.data.CompositionInformation;
import edu.uga.ccrc.ontology.glyco.databasebot.data.GlycanInformation;
import edu.uga.ccrc.ontology.glyco.databasebot.data.ImageInformation;
import edu.uga.ccrc.ontology.glyco.databasebot.data.MassInformation;
import edu.uga.ccrc.ontology.glyco.databasebot.util.ComperatorCompositionInformationMass;
import edu.uga.ccrc.ontology.glyco.databasebot.util.ComperatorGlycanInformationMass;
import edu.uga.ccrc.ontology.glyco.databasebot.util.ComperatorMassInformationMass;
import edu.uga.ccrc.ontology.glyco.databasebot.util.GlycoVisitorTopology;
import edu.uga.ccrc.ontology.glyco.databasebot.util.InformationGenerator;


public class GlycOReportXLS
{
	private static double NA_MASS = 22.98977D;

	private HashMap<Integer,Integer> m_imageWidth = new HashMap<Integer,Integer>();
	private List<HSSFPicture> m_images = new ArrayList<HSSFPicture>();
	private GlycanWorkspace m_gwb = new GlycanWorkspace(null,false,new GlycanRendererAWT());
	private HSSFWorkbook m_workbook = null; 
	private Integer m_currentRow = 0;

	public GlycOReportXLS()
	{
		this.m_gwb.setNotation(GraphicOptions.NOTATION_CFG);
		this.m_gwb.setDisplay(GraphicOptions.DISPLAY_NORMALINFO); 
		this.m_gwb.getGraphicOptions().ORIENTATION = GraphicOptions.RL; 
		this.m_gwb.getGraphicOptions().SHOW_INFO = true; 
		this.m_gwb.getGraphicOptions().SHOW_MASSES = false; 
		this.m_gwb.getGraphicOptions().SHOW_REDEND = false;
	}

	private void clear()
	{
		this.m_imageWidth.clear();
		this.m_images.clear();
		this.m_currentRow = 1;
	}

	public void write(String a_fileName, List<GlycanInformation> a_glycans) throws SugarImporterException, GlycoVisitorException, IOException
	{
		this.m_workbook = new HSSFWorkbook();
		// Structure List
		this.addGlycanList("Structure List", a_glycans);
		// Composition List
		List<CompositionInformation> t_compositions = this.createCompositionView(a_glycans);
		this.addCompositionList("Composition List", t_compositions);
		// mass list
		List<MassInformation> t_massList = this.findMasses(a_glycans, 100D);
		this.addMassList("Mass List", t_massList, true, GraphicOptions.DISPLAY_NORMALINFO);
		// topology
		List<GlycanInformation> t_topologyList = this.createTopologyList(a_glycans);
		t_massList = this.findMasses(t_topologyList, 100D);
		this.addMassList("Topology List", t_massList,false, GraphicOptions.DISPLAY_COMPACT);
		// write the file
		FileOutputStream t_fos = new FileOutputStream(a_fileName);
		this.m_workbook.write(t_fos);
	}

	private List<GlycanInformation> createTopologyList(List<GlycanInformation> a_glycans) throws SugarImporterException, GlycoVisitorException
	{
		Integer t_counter = 0;
		HashMap<String, Boolean> t_topologies = new HashMap<String, Boolean>();
		List<GlycanInformation> t_topologyList = new ArrayList<GlycanInformation>();
		for (GlycanInformation t_glycanInformation : a_glycans) 
		{
			SugarImporterGlycoCTCondensed t_importer = new SugarImporterGlycoCTCondensed();
			Sugar t_sugar = t_importer.parse(t_glycanInformation.getGlycoCT());
			GlycoVisitorTopology t_visitor = new GlycoVisitorTopology();
			t_visitor.start(t_sugar);
			SugarExporterGlycoCTCondensed t_exporter = new SugarExporterGlycoCTCondensed();
			t_exporter.start(t_sugar);
			String t_newSequence = t_exporter.getHashCode();
			if ( t_topologies.get(t_newSequence) == null )
			{
				t_counter++;
				t_topologies.put(t_newSequence, Boolean.TRUE);
				GlycanInformation t_info = t_glycanInformation.clone();
				t_info.setId(null);
				Glycan t_newGlycan = Glycan.fromGlycoCTCondensed(t_newSequence);
				t_info.setGwb(t_newGlycan.toString());
				t_topologyList.add(t_info);
			}
		}
//		System.out.println("Topology counter: " + t_counter.toString());
		return t_topologyList;
	}

	private void addMassList(String a_name, List<MassInformation> a_massList, boolean a_withId, String a_displayStyle)
	{
		this.clear();
		this.m_currentRow = 1;
		this.m_images = new ArrayList<HSSFPicture>();
		HSSFSheet t_sheetStructures = this.m_workbook.createSheet(a_name);
		this.initColumnsMassList(t_sheetStructures, a_massList);
		Collections.sort(a_massList, new ComperatorMassInformationMass());
		for (MassInformation t_info : a_massList)
		{
			this.addMassRows(t_info,t_sheetStructures, a_withId, a_displayStyle);
		}
		for (Integer t_column : this.m_imageWidth.keySet())
		{
			int t_width = (int)(this.m_imageWidth.get(t_column) * 36.6 * 0.5d);
			t_sheetStructures.setColumnWidth( t_column, t_width);
			for (HSSFPicture t_picture : this.m_images)
			{
				t_picture.resize(0.5d);
			}
		}
	}

	private void addMassRows(MassInformation a_info,HSSFSheet a_sheet, boolean a_withId, String a_displayStyle)
	{
		Row t_rowStructure = a_sheet.createRow(this.m_currentRow);
		// mass
		Cell t_cell = t_rowStructure.createCell(0);
		t_cell.setCellValue(a_info.getMass());
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// mass pMe
		t_cell = t_rowStructure.createCell(1);
		t_cell.setCellValue(a_info.getMassPme());
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// pMe + Na
		t_cell = t_rowStructure.createCell(2);
		t_cell.setCellValue(a_info.getMassPme() + GlycOReportXLS.NA_MASS);
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// pMed + 2Na
		t_cell = t_rowStructure.createCell(3);
		t_cell.setCellValue( (a_info.getMassPme() + GlycOReportXLS.NA_MASS + GlycOReportXLS.NA_MASS) / 2D );
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
        // # Structures
        t_cell = t_rowStructure.createCell(4);
        t_cell.setCellValue( a_info.getMembers().size() );
        t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// image
		int t_column = 5;
		for (GlycanInformation t_glycanInfo : a_info.getMembers())
		{
			Row t_rowStructureImage = null;
			try
			{
				ImageInformation t_image = this.createImage(t_glycanInfo.getGwb(), a_displayStyle);
				if ( a_withId )
				{
					t_cell = t_rowStructure.createCell(t_column);
					t_cell.setCellValue(t_glycanInfo.getId());
					t_cell.setCellType(Cell.CELL_TYPE_STRING);
					t_rowStructureImage = a_sheet.createRow(this.m_currentRow+1);
					this.addImageToCell(t_rowStructureImage, t_column, this.m_currentRow + 1, t_glycanInfo, t_image, a_sheet);
				}
				else
				{
					t_rowStructureImage = t_rowStructure;
					this.addImageToCell(t_rowStructureImage, t_column, this.m_currentRow, t_glycanInfo, t_image, a_sheet);
				}
			}
			catch (Exception e)
			{
				t_cell = t_rowStructureImage.createCell(t_column);
				t_cell.setCellValue(e.getMessage());
				t_cell.setCellType(Cell.CELL_TYPE_STRING);
			}
			t_column++;
		}
		if ( a_withId )
		{
			this.m_currentRow += 2;
		}
		else
		{
			this.m_currentRow += 1;
		}
	}

	private void initColumnsMassList(HSSFSheet a_sheet, List<MassInformation> a_glycans)
	{
		Row t_row = a_sheet.createRow(0);

		Cell t_cell = t_row.createCell(0);
		t_cell.setCellValue("Native mass");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 0, 3000);

		t_cell = t_row.createCell(1);
		t_cell.setCellValue("PMe mass");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 1, 3000);

		t_cell = t_row.createCell(2);
		t_cell.setCellValue("PMe Na+");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 2, 3000);

		t_cell = t_row.createCell(3);
		t_cell.setCellValue("PMe 2Na+");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 3, 3000);

		t_cell = t_row.createCell(4);
        t_cell.setCellValue("# Structures");
        t_cell.setCellType(Cell.CELL_TYPE_STRING);
        a_sheet.setColumnWidth( 3, 3000);
	}

	private List<MassInformation> findMasses(List<GlycanInformation> a_glycans, Double a_deviationPPM)
	{
		List<MassInformation> t_result = new ArrayList<MassInformation>();
		for (GlycanInformation t_glycanInfo : a_glycans)
		{
			MassInformation t_info = this.assignToMassInfo(t_glycanInfo,t_result, a_deviationPPM);
			// new mass info created?
			if ( t_info != null )
			{
				t_result.add(t_info);
			}
		}
		return t_result;
	}

	private MassInformation assignToMassInfo(GlycanInformation a_glycanInfo, List<MassInformation> a_result, Double a_defiationPPM)
	{
		for (MassInformation t_massInformation : a_result)
		{
			if ( t_massInformation.isInRange(a_glycanInfo.getMass()) )
			{
				t_massInformation.addMember(a_glycanInfo);
				return null;
			}
		}
		MassInformation t_information = new MassInformation(a_glycanInfo.getMass(), a_defiationPPM);
		t_information.setMassPme(a_glycanInfo.getMassPme());
		t_information.addMember(a_glycanInfo);
		return t_information;
	}

	private void addCompositionList(String a_name, List<CompositionInformation> a_compositions)
	{
		this.m_currentRow = 1;
		this.clear();
		HSSFSheet t_sheetStructures = this.m_workbook.createSheet(a_name);
		HashMap<String,Integer> t_compositionColumns = this.initColumnsCompositionList(t_sheetStructures, a_compositions);
		Collections.sort(a_compositions, new ComperatorCompositionInformationMass());
		for (CompositionInformation t_info : a_compositions)
		{
			this.addCompositionRows(t_info,t_sheetStructures,t_compositionColumns);
		}
	}

	private void addCompositionRows(CompositionInformation a_info,HSSFSheet a_sheet, HashMap<String,Integer> a_compositionColumns)
	{
		Row t_rowStructure = a_sheet.createRow(this.m_currentRow);
		// mass
		Cell t_cell = t_rowStructure.createCell(0);
		t_cell.setCellValue(a_info.getMass());
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// mass pMe
		t_cell = t_rowStructure.createCell(1);
		t_cell.setCellValue(a_info.getMassPme());
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// pMe + Na
		t_cell = t_rowStructure.createCell(2);
		t_cell.setCellValue(a_info.getMassPme() + GlycOReportXLS.NA_MASS);
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// pMed + 2Na
		t_cell = t_rowStructure.createCell(3);
		t_cell.setCellValue( (a_info.getMassPme() + GlycOReportXLS.NA_MASS + GlycOReportXLS.NA_MASS) / 2D );
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// composition
		t_cell = t_rowStructure.createCell(4);
		t_cell.setCellValue(a_info.getCompositionName());
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		// Number of structures
		t_cell = t_rowStructure.createCell(5);
		t_cell.setCellValue(a_info.getMembers().size());
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// composition
		HashMap<String, Integer> t_compostionHash = a_info.getComposition();
		for (String t_component : t_compostionHash.keySet())
		{
			t_cell = t_rowStructure.createCell(a_compositionColumns.get(t_component));
			t_cell.setCellValue(t_compostionHash.get(t_component));
			t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		}
		this.m_currentRow++;
	}

	private HashMap<String,Integer> initColumnsCompositionList(HSSFSheet a_sheet, List<CompositionInformation> a_glycans)
	{
		Row t_row = a_sheet.createRow(0);

		Cell t_cell = t_row.createCell(0);
		t_cell.setCellValue("Native mass");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 0, 3000);

		t_cell = t_row.createCell(1);
		t_cell.setCellValue("PMe mass");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 1, 3000);

		t_cell = t_row.createCell(2);
		t_cell.setCellValue("PMe Na+");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 2, 3000);

		t_cell = t_row.createCell(3);
		t_cell.setCellValue("PMe 2Na+");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 3, 3000);

		t_cell = t_row.createCell(4);
		t_cell.setCellValue("Composition");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 4, 12000);

		t_cell = t_row.createCell(5);
		t_cell.setCellValue("# Structures");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 5, 3000);

		HashMap<String,Integer> t_compColComposition = this.createCompositionColumnFromComposition(5, a_glycans);

		for (String t_component : t_compColComposition.keySet())
		{
			t_cell = t_row.createCell(t_compColComposition.get(t_component));
			t_cell.setCellValue(InformationGenerator.formatComposition(t_component));
			t_cell.setCellType(Cell.CELL_TYPE_STRING);
		}
		return t_compColComposition;
	}

	private List<CompositionInformation> createCompositionView(List<GlycanInformation> a_glycans)
	{
		HashMap<String, CompositionInformation> t_compositionView = new HashMap<String, CompositionInformation>();
		for (GlycanInformation t_glycanInformation : a_glycans)
		{
			CompositionInformation t_collection = t_compositionView.get(t_glycanInformation.getCompositionName());
			if ( t_collection == null )
			{
				t_collection = new CompositionInformation();
				t_collection.setComposition(t_glycanInformation.getComposition());
				t_collection.setCompositionString(t_glycanInformation.getCompositionName());
				t_collection.setMass(t_glycanInformation.getMass());
				t_collection.setMassPme(t_glycanInformation.getMassPme());
				t_compositionView.put(t_glycanInformation.getCompositionName(), t_collection);
			}
			t_collection.addMember(t_glycanInformation);
		}
		List<CompositionInformation> t_result = new ArrayList<CompositionInformation>();
		for (String t_composition : t_compositionView.keySet())
		{
			t_result.add(t_compositionView.get(t_composition));
		}
		return t_result;
	}

	private void addGlycanList(String a_name, List<GlycanInformation> a_glycans)
	{
		this.clear();
		this.m_currentRow = 1;
		HSSFSheet t_sheetStructures = this.m_workbook.createSheet(a_name);
		HashMap<String,Integer> t_compositionColumns = this.initColumnsGlycanList(t_sheetStructures, a_glycans);
		Collections.sort(a_glycans, new ComperatorGlycanInformationMass());
		for (GlycanInformation t_info : a_glycans)
		{
			this.addGlycanRows(t_info,t_sheetStructures,t_compositionColumns);
		}
		for (Integer t_column : this.m_imageWidth.keySet())
		{
			int t_width = (int)(this.m_imageWidth.get(t_column) * 36.6 * 0.5d);
			t_sheetStructures.setColumnWidth( t_column, t_width);
			for (HSSFPicture t_picture : this.m_images)
			{
				t_picture.resize(0.5d);
			}
		}
	}

	private void addGlycanRows(GlycanInformation a_info,HSSFSheet a_sheet, HashMap<String,Integer> a_compositionColumns)
	{
		Row t_rowStructure = a_sheet.createRow(this.m_currentRow);
		// mass
		Cell t_cell = t_rowStructure.createCell(0);
		t_cell.setCellValue(a_info.getMass());
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// mass pMe
		t_cell = t_rowStructure.createCell(1);
		t_cell.setCellValue(a_info.getMassPme());
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// pMe + Na
		t_cell = t_rowStructure.createCell(2);
		t_cell.setCellValue(a_info.getMassPme() + GlycOReportXLS.NA_MASS);
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// pMed + 2Na
		t_cell = t_rowStructure.createCell(3);
		t_cell.setCellValue( (a_info.getMassPme() + GlycOReportXLS.NA_MASS + GlycOReportXLS.NA_MASS) / 2D );
		t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		// composition
		t_cell = t_rowStructure.createCell(4);
		t_cell.setCellValue(a_info.getCompositionName());
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		// glycan ID
		t_cell = t_rowStructure.createCell(5);
		t_cell.setCellValue(a_info.getId());
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		// image
		try
		{
			ImageInformation t_image = this.createImage(a_info.getGwb(), GraphicOptions.DISPLAY_NORMALINFO);
			this.addImageToCell(t_rowStructure, 6, this.m_currentRow, a_info, t_image, a_sheet);
		}
		catch (Exception e)
		{
			t_cell = t_rowStructure.createCell(6);
			t_cell.setCellValue(e.getMessage());
			t_cell.setCellType(Cell.CELL_TYPE_STRING);
		}
		// composition
		HashMap<String, Integer> t_compostionHash = a_info.getComposition();
		for (String t_component : t_compostionHash.keySet())
		{
			t_cell = t_rowStructure.createCell(a_compositionColumns.get(t_component));
			t_cell.setCellValue(t_compostionHash.get(t_component));
			t_cell.setCellType(Cell.CELL_TYPE_NUMERIC);
		}
		this.m_currentRow++;
	}

	private ImageInformation createImage(String a_sequence, String a_displayStyle) throws Exception
	{
		this.m_gwb.setDisplay(a_displayStyle);
		BufferedImage img = this.m_gwb.getGlycanRenderer().getImage(new Union<Glycan>(Glycan.fromString(a_sequence)),true,false,false);
		if ( img.getHeight() > 0 )
		{
			ImageInformation t_image = new ImageInformation();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			javax.imageio.ImageIO.write(img,"png",bos);
			t_image.setImage(bos.toByteArray());
			t_image.setWidth(img.getWidth());
			t_image.setHeight(img.getHeight());
			return t_image;
		}
		else
		{
			throw new Exception("Unable to create image.");
		}
	}

	private void addImageToCell(Row a_row, int a_columnNumber, int a_rowNumber, GlycanInformation a_info, ImageInformation a_image, HSSFSheet a_sheet)
	{
		// write the image
		HSSFClientAnchor anchor = new HSSFClientAnchor( 0, 0, 0, 0, (short)a_columnNumber, a_rowNumber, (short)(a_columnNumber + 1), a_rowNumber + 1);
		int index = this.m_workbook.addPicture(a_image.getImage(),HSSFWorkbook.PICTURE_TYPE_PNG);
		HSSFPatriarch patriarch = a_sheet.createDrawingPatriarch();
		this.m_images.add(patriarch.createPicture(anchor,index));
		anchor.setAnchorType(3);
		a_row.setHeightInPoints( (int)(0.5d*0.76*a_image.getHeight())+2 );
		Integer t_maxImageWidth = this.m_imageWidth.get(a_columnNumber);
		if ( t_maxImageWidth == null )
		{
			this.m_imageWidth.put( a_columnNumber, a_image.getWidth());
		}
		else if ( a_image.getWidth() > t_maxImageWidth )
		{
			this.m_imageWidth.put( a_columnNumber, a_image.getWidth());
		}
	}

	private HashMap<String,Integer> initColumnsGlycanList(HSSFSheet a_sheet, List<GlycanInformation> a_glycans)
	{
		Row t_row = a_sheet.createRow(0);

		Cell t_cell = t_row.createCell(0);
		t_cell.setCellValue("Native mass");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 0, 3000);

		t_cell = t_row.createCell(1);
		t_cell.setCellValue("PMe mass");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 1, 3000);

		t_cell = t_row.createCell(2);
		t_cell.setCellValue("PMe Na+");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 2, 3000);

		t_cell = t_row.createCell(3);
		t_cell.setCellValue("PMe 2Na+");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 3, 3000);

		t_cell = t_row.createCell(4);
		t_cell.setCellValue("Composition");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 4, 12000);

		t_cell = t_row.createCell(5);
		t_cell.setCellValue("GlycO ID");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);
		a_sheet.setColumnWidth( 5, 3000);

		t_cell = t_row.createCell(6);
		t_cell.setCellValue("Cartoon");
		t_cell.setCellType(Cell.CELL_TYPE_STRING);

		HashMap<String,Integer> t_compColComposition = this.createCompositionColumnFromGlycan(6, a_glycans);

		for (String t_component : t_compColComposition.keySet())
		{
			t_cell = t_row.createCell(t_compColComposition.get(t_component));
			t_cell.setCellValue(InformationGenerator.formatComposition(t_component));
			t_cell.setCellType(Cell.CELL_TYPE_STRING);
		}
		return t_compColComposition;
	}

	private HashMap<String, Integer> createCompositionColumnFromGlycan(int a_startPosition, List<GlycanInformation> a_glycans)
	{
		ArrayList<String> t_components = new ArrayList<String>();
		HashMap<String, Integer> t_result = new HashMap<String, Integer>();
		for (GlycanInformation t_info : a_glycans)
		{
			HashMap<String, Integer> t_compostion = t_info.getComposition();
			for (String t_comp : t_compostion.keySet())
			{
				if ( t_result.get(t_comp) == null )
				{
					t_result.put(t_comp, 0);
					t_components.add(t_comp);
				}
			}
		}
		Collections.sort(t_components);
		int t_position = a_startPosition+1;
		for (String t_string : t_components)
		{
			t_result.put(t_string, t_position);
			t_position++;
		}
		return t_result;
	}

	private HashMap<String, Integer> createCompositionColumnFromComposition(int a_startPosition, List<CompositionInformation> a_glycans)
	{
		ArrayList<String> t_components = new ArrayList<String>();
		HashMap<String, Integer> t_result = new HashMap<String, Integer>();
		for (CompositionInformation t_info : a_glycans)
		{
			HashMap<String, Integer> t_compostion = t_info.getComposition();
			for (String t_comp : t_compostion.keySet())
			{
				if ( t_result.get(t_comp) == null )
				{
					t_result.put(t_comp, 0);
					t_components.add(t_comp);
				}
			}
		}
		Collections.sort(t_components);
		int t_position = a_startPosition+1;
		for (String t_string : t_components)
		{
			t_result.put(t_string, t_position);
			t_position++;
		}
		return t_result;
	}
}
