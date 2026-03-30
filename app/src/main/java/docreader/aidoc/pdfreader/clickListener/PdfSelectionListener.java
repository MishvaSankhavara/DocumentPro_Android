package docreader.aidoc.pdfreader.clickListener;


import docreader.aidoc.pdfreader.model_reader.PDFReaderModel;

public interface PdfSelectionListener {

    void onPdfSelect(PDFReaderModel pdfModel_listener, int position_listener);
}
