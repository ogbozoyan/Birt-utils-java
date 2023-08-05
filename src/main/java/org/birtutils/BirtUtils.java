package org.birtutils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.report.model.api.*;
import org.eclipse.birt.report.model.api.activity.SemanticException;
import org.eclipse.birt.report.model.api.command.ContentException;
import org.eclipse.birt.report.model.api.command.NameException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class wrapper of dummy std birtruntime library with absolutely unenxected methods and them usage
 *
 * @author ogbozoyan
 * @since 05.08.2023
 */
@Slf4j
public class BirtUtils {
    /**
     * Adds a new grid to the report design.
     * <p>
     * This method allows adding a new grid to the report design's body. A grid is a layout element used to organize data
     * in a tabular format. The grid is added to the report design handle's body.
     *
     * @param designHandle The handle to the report design where the grid will be added.
     * @param grid         The grid handle to be added to the report design.
     * @throws NameException    If there is an issue with the name of the grid or another element.
     * @throws ContentException If there is an issue with the content or structure of the report design.
     * @since 1.0
     */
    public void addNewGridToDesign(ReportDesignHandle designHandle, GridHandle grid) throws NameException, ContentException {
        if (grid != null) {
            designHandle.getBody().add(grid);
        }
    }

    /**
     * Adds a RowHandle to a GridHandle in a BIRT report design.
     *
     * @param grid The GridHandle to which the row will be added.
     * @param row  The RowHandle to be added to the grid.
     * @throws NameException    if there is an issue with the BIRT element names.
     * @throws ContentException if there is an issue with the BIRT element content.
     */
    public void addRowToGrid(GridHandle grid, RowHandle row) throws NameException, ContentException {
        if (grid != null && row != null) {
            grid.getRows().add(row);
        }
    }

    /**
     * Adds a CellHandle to a RowHandle in a BIRT report design.
     *
     * @param row  The RowHandle to which the cell will be added.
     * @param cell The CellHandle to be added to the row.
     * @throws NameException    if there is an issue with the BIRT element names.
     * @throws ContentException if there is an issue with the BIRT element content.
     */
    public void addCellToRow(RowHandle row, CellHandle cell) throws NameException, ContentException {
        if (row != null && cell != null) {
            row.getCells().add(cell);
        }
    }

    /**
     * Adds a TextItemHandle to a CellHandle in a BIRT report design.
     *
     * @param textItemHandle The TextItemHandle to be added to the cell.
     * @param cell           The CellHandle to which the text will be added.
     * @throws NameException    if there is an issue with the BIRT element names.
     * @throws ContentException if there is an issue with the BIRT element content.
     */
    public void addTextToCell(TextItemHandle textItemHandle, CellHandle cell) throws NameException, ContentException {
        if (textItemHandle != null && cell != null) {
            cell.getContent().add(textItemHandle);
        }
    }

    /**
     * Sets properties for a CellHandle to customize its appearance.
     *
     * @param cell The CellHandle to which the properties will be applied.
     * @throws SemanticException if there is an issue with the BIRT design semantics.
     */
    public void setPropertyToCell(CellHandle cell) throws SemanticException {
        if (cell == null) {
            log.debug("CellHandle is null");
            return;
        }
        cell.setProperty("borderBottomStyle", "solid");
        cell.setProperty("borderBottomWidth", "1px");
        cell.setProperty("borderLeftStyle", "solid");
        cell.setProperty("borderLeftWidth", "1px");
        cell.setProperty("borderRightStyle", "solid");
        cell.setProperty("borderRightWidth", "1px");
        cell.setProperty("borderTopStyle", "solid");
        cell.setProperty("borderTopWidth", "1px");
    }

    /**
     * Sets properties for a TextItemHandle to customize its appearance.
     *
     * @param text The TextItemHandle to which the properties will be applied.
     * @throws SemanticException if there is an issue with the BIRT design semantics.
     */
    public void setPropertyToText(TextItemHandle text) throws SemanticException {
        if (text == null) {
            log.debug("TextItemHandler is null");
            return;
        }
        text.setProperty("fontFamily", "Times New Roman");
        text.setProperty("fontSize", "12pt");
        text.setProperty("contentType", "plain"); // available values: plain, html, auto
    }

    /**
     * Sets up the output format for a BIRT report task based on the desired format and output stream.
     *
     * @param task         The IRunAndRenderTask for which the output format will be set.
     * @param format       The OutputFormat indicating the desired format for the report output.
     * @param outputStream The ByteArrayOutputStream to which the report content will be written.
     */
    public void setUpOutPutFormat(IRunAndRenderTask task, OutputFormat format, ByteArrayOutputStream outputStream) throws BirtException {
        IRenderOption options = new RenderOption();
        PDFRenderOption pdfOptions = new PDFRenderOption();
        pdfOptions.setOption(IPDFRenderOption.REPAGINATE_FOR_PDF, Boolean.TRUE);
        HTMLRenderOption htmlRenderOption = new HTMLRenderOption();
        htmlRenderOption.setEmbeddable(true);

        EXCELRenderOption excelRenderOption = new EXCELRenderOption();
        switch (format.getFormat()) {
            case ("docx") -> {
                options.setOutputFormat("docx");
                options.setOutputStream(outputStream);
                options.setOption(RenderOption.LOCALE, "ru_RU");
                task.setRenderOption(options);
                log.debug("Set up output format -> docx");
            }
            case ("xlsx") -> {
                excelRenderOption.setOutputFormat("xlsx"); // Set the output format to XLSX
                excelRenderOption.setOutputStream(outputStream);
                task.setRenderOption(excelRenderOption);
                log.debug("Set up output format -> xlsx");
            }
            case ("pdf") -> {
                pdfOptions.setOutputFormat("pdf");
                pdfOptions.setOutputStream(outputStream);
                task.setRenderOption(pdfOptions);
                log.debug("Set up output format -> pdf");
            }
            case ("odt") -> {
                options.setOutputFormat("odt");
                options.setOutputStream(outputStream);
                task.setRenderOption(options);
                log.debug("Set up output format -> odt");
            }
            case ("rtf") -> {
                options.setOutputFormat("rtf");
                options.setOutputStream(outputStream);
                task.setRenderOption(options);
                log.debug("Set up output format -> rtf");
            }
            case ("html") -> {
                htmlRenderOption.setOutputFormat("html"); // Set the output format to HTML
                htmlRenderOption.setOutputStream(outputStream);
                task.setRenderOption(htmlRenderOption);
                log.debug("Set up output format -> html");
            }
            default -> throw new BirtException("Unsupported format: " + format);
        }
    }

    /**
     * Retrieves a BIRT report item from a report design based on its name.
     *
     * @param reportDesign The ReportDesignHandle of the BIRT report.
     * @param itemName     The name of the BIRT report item to retrieve.
     * @return The BIRT report item (GridHandle or TextItemHandle) with the specified name, or null if not found.
     */
    public Object getItemByName(ReportDesignHandle reportDesign, String itemName) {
        DesignElementHandle element = reportDesign.findElement(itemName);
        if (element != null) {
            if (element instanceof GridHandle || element instanceof TextItemHandle) {
                log.debug("Found element '" + itemName + "' of type: " + element.getClass().getSimpleName());
                return element;
            } else {
                log.debug("Found element '" + itemName + "' but it is not a GridHandle or TextItemHandle.");
            }
        } else {
            log.debug("Element '" + itemName + "' not found in the report design.");
        }
        return null;
    }

    /**
     * Gets a BIRT report file from the filesystem based on its name.
     *
     * @param reportName The name of the BIRT report file to retrieve from the filesystem.
     * @return The File object representing the BIRT report file.
     * @throws BirtException if the report file does not exist or is not readable.
     */
    public InputStream getReportFromClassPath(String reportName) throws BirtException {
        log.debug("Getting report from filesystem {}", reportName);
        try {
            URL url = getClass().getResource(reportName);
            InputStream inputStream;
            if (url != null) {
                inputStream = url.openStream();
            } else {
                log.error("Report {} either did not exist or was not readable.", reportName);
                throw new BirtException("Report " + reportName + " either did not exist or was not readable.");
            }
            if (inputStream == null) {
                log.error("Report {} either did not exist or was not readable.", reportName);
                throw new BirtException("Report " + reportName + " either did not exist or was not readable.");
            }
            return inputStream;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error while retrieving report {}", reportName);
            throw new BirtException("Error while retrieving report " + reportName);
        }
    }

    /**
     * Retrieves the ReportDesignHandle from an IReportRunnable.
     *
     * @param runnable The IReportRunnable for which the ReportDesignHandle will be retrieved.
     * @return The ReportDesignHandle associated with the IReportRunnable, or null if not found.
     */
    @SuppressWarnings("deprecation")
    public ReportDesignHandle getReportDesignFromRunnable(IReportRunnable runnable) {
        if (runnable == null)
            return null;
        else return runnable.getDesignHandle().getDesignHandle();
    }


    /**
     * Retrieves the rows from a given GridHandle in the BIRT report design.
     *
     * @param gridHandle The GridHandle object representing the grid in the BIRT report design.
     * @return A List of RowHandle objects representing the rows in the grid. Returns null if the input GridHandle is null.
     */
    public List<RowHandle> getGridRows(GridHandle gridHandle) {
        if (gridHandle == null) {
            return null;
        }
        List<RowHandle> rows = new ArrayList<>();
        SlotHandle slot = gridHandle.getSlot(GridHandle.ROW_SLOT);
        if (slot != null) {
            for (int i = 0; i < slot.getCount(); i++) {
                if (slot.get(i) instanceof RowHandle rowHandle) {
                    rows.add(rowHandle);
                }
            }
        }
        return rows;
    }

    /**
     * Retrieves the cells from a given RowHandle in the BIRT report design.
     *
     * @param rowHandle The GridHandle object representing the grid in the BIRT report design.
     * @return A List of CellHandle objects representing the rows in the grid. Returns null if the input RowHandle is null.
     */
    @SuppressWarnings("unchecked")
    public List<CellHandle> getRowCells(RowHandle rowHandle) {
        if (rowHandle == null) {
            return null;
        }
        List<CellHandle> cells = new ArrayList<>();
        SlotHandle slot = rowHandle.getCells();
        List<DesignElementHandle> elements = slot.getContents();
        for (DesignElementHandle element : elements) {
            if (element instanceof CellHandle cell) {
                cells.add(cell);
            }
        }
        return cells;
    }

    /**
     * Creates a new RowHandle in a BIRT report design.
     *
     * @param designHandle The ReportDesignHandle used to create BIRT elements.
     * @return The newly created RowHandle.
     */
    public RowHandle createNewRow(ReportDesignHandle designHandle) {
        if (designHandle == null)
            return null;
        else return designHandle.getElementFactory().newTableRow();
    }

    /**
     * Creates a new CellHandle in a BIRT report design.
     *
     * @param designHandle The ReportDesignHandle used to create BIRT elements.
     * @return The newly created CellHandle.
     */
    public CellHandle createNewCell(ReportDesignHandle designHandle) {
        if (designHandle == null)
            return null;
        else return designHandle.getElementFactory().newCell();
    }

    /**
     * Creates a new TextItemHandle in a BIRT report design.
     *
     * @param designHandle The ReportDesignHandle used to create BIRT elements.
     * @return The newly created TextItemHandle.
     */
    public TextItemHandle createNewTextItem(ReportDesignHandle designHandle) {
        if (designHandle == null)
            return null;
        else return designHandle.getElementFactory().newTextItem(null);
    }

    /**
     * Creates a new grid and adds it to the report design.
     * <p>
     * This method creates a new grid with the specified name, number of rows, and number of columns. The grid is added to
     * the report design handle, which represents the report design. The grid is initialized with default properties such
     * as font family, font size, text alignment, and height.
     *
     * @param designHandle The handle to the report design where the new grid will be added.
     * @param name         The name of the new grid.
     * @param rows         The number of rows in the new grid.
     * @param columns      The number of columns in the new grid.
     * @return The grid handle representing the newly created grid.
     * @throws SemanticException If there is an issue with the semantic content of the report design.
     * @since 1.0
     */
    public GridHandle createNewGrid(ReportDesignHandle designHandle, String name, int rows, int columns) throws SemanticException {
        GridHandle gridHandle = designHandle.getElementFactory().newGridItem(name, rows, columns);
        gridHandle.setProperty("fontFamily", "Times New Roman");
        gridHandle.setProperty("fontSize", "12pt");
        gridHandle.setProperty("textAlign", "center");
        gridHandle.setProperty("height", "10cm");
        gridHandle.setProperty("borderBottomStyle", "solid");
        gridHandle.setProperty("borderBottomWidth", "1px");
        gridHandle.setProperty("borderLeftStyle", "solid");
        gridHandle.setProperty("borderLeftWidth", "1px");
        gridHandle.setProperty("borderRightStyle", "solid");
        gridHandle.setProperty("borderRightWidth", "1px");
        gridHandle.setProperty("borderTopStyle", "solid");
        gridHandle.setProperty("borderTopWidth", "1px");
        return gridHandle;
    }

    /**
     * Creates a new cell in a row with the given text to place.
     * Order of cells is from left to right. Must be used in the order in which you want to arrange the cells in the row.
     *
     * @param designHandle The ReportDesignHandle used to create BIRT elements.
     * @param row          The RowHandle to which the cell will be added.
     * @param textToPlace  The text content to place in the cell.
     * @throws SemanticException if there is an issue with the BIRT design semantics.
     */
    public void createCellWithTextToRow(ReportDesignHandle designHandle, RowHandle row, String textToPlace) throws SemanticException {
        if (row == null) {
            log.debug("RowHandle is null");
            return;
        }
        CellHandle cell = createNewCell(designHandle);
        TextItemHandle textItem = createNewTextItem(designHandle);
        textItem.setContent(textToPlace);
        setPropertyToText(textItem);
        addTextToCell(textItem, cell);
        setPropertyToCell(cell);
        addCellToRow(row, cell);
    }


    @Getter
    public enum OutputFormat {
        PDF("pdf"),
        DOCX("docx"),
        XLSX("xlsx"),
        HTML("html"),
        ODT("odt");

        private final String format;

        OutputFormat(String format) {
            this.format = format;
        }

    }
}
