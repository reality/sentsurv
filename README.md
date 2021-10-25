Files for sentiment survey analysis.

You can run your own analysis on an ontology's metadata. First you have to use Komenti, 
which you can get from https://github.com/reality/Komenti , and retrieve the labels
and metadata. Here's an example for DO:

```bash
komenti query --query disease --ontology DOID --out disease_ontology_terms.tsv
komenti get_metadata -l disease_ontology_terms.tsv --field definition --out metadata
```

The definitions will be in the metadata subdirectory, and the classes will be in 
disease_ontology_terms.tsv.

You can then run the following to get sentiments scores for all the classes.

```bash
groovy get_sentiments
```

The resulting sentiments will be in sentiments.txt. do_all_subclasses will propagate sentiments across hierarchy, and calc_averages will average the score, but these scripts will need some modification because they currently rely on merged files for the experiment given in the current paper. Feel free to message me for input, but these scripts will be generalised for a future publication :)

Results are given, too, for MESH and DOID, which you can see in propagated_ontology_sentiments.tsv. Evaluation was performed in R, and commands are shown in eval.R.
