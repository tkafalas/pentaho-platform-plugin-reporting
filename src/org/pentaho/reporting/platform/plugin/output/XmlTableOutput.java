package org.pentaho.reporting.platform.plugin.output;

import java.io.IOException;
import java.io.OutputStream;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.YieldReportListener;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableOutputProcessor;
import org.pentaho.reporting.libraries.repository.ContentIOException;

public class XmlTableOutput implements ReportOutputHandler
{
  private StreamReportProcessor proc;
  private ProxyOutputStream proxyOutputStream;

  public XmlTableOutput()
  {
  }

  public Object getReportLock()
  {
    return this;
  }

  private StreamReportProcessor createProcessor(final MasterReport report,
                                                final int yieldRate)
      throws ReportProcessingException
  {
    proxyOutputStream = new ProxyOutputStream();
    final XmlTableOutputProcessor target = new XmlTableOutputProcessor(proxyOutputStream);
    final StreamReportProcessor proc = new StreamReportProcessor(report, target);

    if (yieldRate > 0)
    {
      proc.addReportProgressListener(new YieldReportListener(yieldRate));
    }
    return proc;
  }

  public int paginate(MasterReport report, int yieldRate) throws ReportProcessingException, IOException, ContentIOException {
    return 0;
  }
  
  public int generate(final MasterReport report,
                          final int acceptedPage,
                          final OutputStream outputStream,
                          final int yieldRate) throws ReportProcessingException, IOException
  {
    try
    {
      if (proc == null)
      {
        proc = createProcessor(report, yieldRate);
      }

      proxyOutputStream.setParent(outputStream);
      proc.processReport();
      return 0;
    }
    finally
    {
      proxyOutputStream.setParent(null);
      outputStream.flush();
    }
  }

  public boolean supportsPagination() {
    return false;
  }

  public void close()
  {
    if (proc != null)
    {
      proc.close();
      proc = null;
      proxyOutputStream = null;
    }

  }
}
