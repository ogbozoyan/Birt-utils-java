package org.birtutils;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;
import org.eclipse.birt.report.model.api.GridHandle;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.birt.report.model.api.RowHandle;
import org.eclipse.core.internal.registry.RegistryProviderFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author ogbozoyan ogbozoyan@mail.ru
 * @since 17.07.2023
 * =====================================================================================================================
 * В репортах следуют использовать следующий нейминг  In reports should be used the following naming convention:
 * Тип(Type) | нейминг(name)
 * Grid | gridNameGrid
 * Text | textNameText
 * Dynamic Text | textNameDText
 * Param | paramName
 * Param (not inserted in text DText) | paramNameDText
 * <p>
 * Если случается ошибка версий репортов - надо открыть репорт и в теге <report> заменить на
 * If causes error versions of reports - need to open report and in the tag <report> change to
 * <report xmlns="http://www.eclipse.org/birt/2005/design" version="3.2.23" id="1">
 * =====================================================================================================================
 */
@Slf4j
public class Main {

    public static void main(String[] args) throws BirtException {

        IReportEngineFactory reportEngineFactory = null;
        IReportEngine birtReportEngine = null;

        BirtUtils birtUtils = new BirtUtils();

        try {

            EngineConfig engineConfig = new EngineConfig();

            RegistryProviderFactory.releaseDefault();
            Platform.startup(engineConfig);
            reportEngineFactory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            birtReportEngine = reportEngineFactory.createReportEngine(engineConfig);
            log.info("BIRT Report engine started successfully.");

        } catch (BirtException e) {
            e.printStackTrace();
            throw new BirtException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        InputStream rptDesignFile;
        IRunAndRenderTask task = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        IReportRunnable runnable;
        //from resources
        String reportPath = "/example.rptdesign";
        try {
            log.debug("=========================| Strarting Rendering assign report |=========================");
            rptDesignFile = birtUtils.getReportFromClassPath(reportPath);
            runnable = birtReportEngine.openReportDesign(rptDesignFile);
            task = birtReportEngine.createRunAndRenderTask(runnable);
            birtUtils.setUpOutPutFormat(task, BirtUtils.OutputFormat.DOCX, byteArrayOutputStream);

            ReportDesignHandle reportDesignFromRunnable = birtUtils.getReportDesignFromRunnable(runnable);

            //Getting grids by name
            GridHandle mainGrid = (GridHandle) birtUtils.getItemByName(reportDesignFromRunnable, "mainGrid");
            assert mainGrid != null;
            GridHandle secondGrid = (GridHandle) birtUtils.getItemByName(reportDesignFromRunnable, "secondGrid");
            assert secondGrid != null;

            //Setup report parameters
            task.setParameterValue("parametrFirst", "First parameter");
            task.setParameterValue("parametrSecond", "Second parameter");

            //Dynamic extend Grid
            RowHandle newRow = birtUtils.createNewRow(reportDesignFromRunnable);

            birtUtils.createCellWithTextToRow(reportDesignFromRunnable, newRow, "new row wow cow pow");
            birtUtils.addRowToGrid(secondGrid, newRow);

            //render
            task.run();


            try (OutputStream outFile = new FileOutputStream("output.docx")) {
                byteArrayOutputStream.writeTo(outFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new BirtException(e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            log.debug("=========================| Finished Rendering assign report |=========================");
            if (task != null) {
                task.close();
            }
        }

        try {
            log.info("Shutting down BIRT Report engine...");
            birtReportEngine.destroy();
            log.info("BIRT Report engine destroyed successfully.");

            log.info("Releasing default registry provider...");
            RegistryProviderFactory.releaseDefault();
            log.info("Default registry provider released successfully.");

            log.info("Shutting down BIRT Platform...");
            Platform.shutdown();
            log.info("BIRT Platform shutdown completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            throw new BirtException(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
