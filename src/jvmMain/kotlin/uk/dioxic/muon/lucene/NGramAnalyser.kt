package uk.dioxic.muon.lucene

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.CharArraySet
import org.apache.lucene.analysis.TokenStream
import org.apache.lucene.analysis.core.LowerCaseFilter
import org.apache.lucene.analysis.core.StopFilter
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter
import org.apache.lucene.analysis.ngram.NGramTokenFilter
import org.apache.lucene.analysis.standard.StandardTokenizer

class NGramAnalyser(private val stopWords: CharArraySet) : Analyzer() {
    override fun createComponents(fieldName: String?): TokenStreamComponents {
        val source = StandardTokenizer()
        val tokenStream = source
            .add { LowerCaseFilter(it) }
            .add { StopFilter(it, stopWords) }
            .add { ASCIIFoldingFilter(it) }
            .add { NGramTokenFilter(it, 3, 5, true) }

        return TokenStreamComponents(source, tokenStream)
    }

    private fun TokenStream.add(block: (TokenStream) -> TokenStream) = block(this)
}