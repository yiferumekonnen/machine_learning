# Data Visualization Examples and Student Performance Analysis in Python

## Overview

This repository contains a collection of Jupyter notebooks demonstrating various data visualization techniques using popular Python libraries like `matplotlib`, `seaborn`, `squarify`, and `numpy`. It serves two main purposes:

1.  **Showcase Diverse Chart Types:** The `different_chart_of_visualization.ipynb` notebook acts as a gallery, providing simple, self-contained examples of creating common charts.
2.  **Apply Visualization to Real Data:** The `datavisualization_project_1.ipynb` notebook performs exploratory data analysis (EDA) and visualization on a "StudentsPerformance.csv" dataset to uncover insights about factors influencing student scores.
3.  **Demonstrate Control Charts:** The `controlchart_class_lab.ipynb` notebook focuses specifically on creating X-bar and P-charts for statistical process control examples.

## File Descriptions

1.  **`different_chart_of_visualization.ipynb`**
    *   **Purpose:** To illustrate the creation of various standard plots.
    *   **Content:** Contains code and output for:
        *   Bar Chart (Population by Country)
        *   Line Chart (Monthly Sales Trend)
        *   Pie Chart (Fruit Market Share)
        *   Scatter Plot (Height vs Weight relationship)
        *   Histogram (Score Distribution)
        *   Heatmap (Correlation/Matrix data example)
        *   Bubble Chart (X vs Y with bubble size representing a third variable)
        *   Tree Map (Proportional area representation - Country Size)
        *   Box Plot (Data distribution, quartiles, outliers)
        *   Color Highlighted Bar Chart (Emphasizing data with color)
        *   *Note:* Includes an attempt at a `geopandas` world map visualization, which may require additional setup or data downloading due to library changes.
    *   **Data:** Uses small, inline datasets defined within the notebook.
    *   **Libraries:** Primarily `matplotlib`, `seaborn`, `numpy`, `squarify`.

2.  **`datavisualization_project_1.ipynb`**
    *   **Purpose:** To apply EDA and visualization techniques to understand the `StudentsPerformance.csv` dataset.
    *   **Content:**
        *   Loads the dataset using `pandas`.
        *   Performs initial data exploration: checks shape, data types, missing values, and generates summary statistics (`describe`).
        *   Creates visualizations to explore relationships:
            *   Box plots showing score distributions based on 'test preparation course' and 'lunch' type.
            *   Bar charts comparing average 'math', 'reading', and 'writing' scores across 'gender', 'test preparation course', and 'parental level of education'.
            *   Histograms illustrating the distribution of 'math scores' grouped by 'lunch' type, using density plots (KDE).
            *   A correlation heatmap showing the relationships between the three score types.
        *   Calculates a 'total_score' column by summing the three individual scores.
        *   Includes an additional, separate example demonstrating an X-bar control chart using different sample data (average time spent on homework).
    *   **Data:** Relies on the external `StudentsPerformance.csv` file. **The file path needs to be updated in the notebook to match its location on your system.**
    *   **Libraries:** `pandas`, `numpy`, `matplotlib`, `seaborn`.

3.  **`controlchart_class_lab.ipynb`**
    *   **Purpose:** To demonstrate the creation of Statistical Process Control (SPC) charts.
    *   **Content:**
        *   Plots an X-bar chart: Visualizes the weekly average time spent on class/homework by two groups over 10 weeks, including the overall mean and Upper/Lower Control Limits (UCL/LCL).
        *   Plots a P-chart: Visualizes the weekly proportion of late students over 20 days, including the average proportion (p-bar) and UCL/LCL.
    *   **Data:** Uses inline list data for demonstration.
    *   **Libraries:** `matplotlib`, `numpy`.

## Visualizations Included

This repository showcases the following types of visualizations:

*   📊 **Bar Charts:** Simple, Grouped, Color-highlighted
*   📈 **Line Charts:** Trends over time
*   🥧 **Pie Charts:** Proportional representation
*   🎯 **Scatter Plots:** Relationship between two continuous variables
*   📊 **Histograms:** Data distribution, Density plots (KDE), Grouped
*   📊 **Box Plots:** Distribution summary, Quartiles, Outliers, Grouped
*   🔥 **Heatmaps:** Correlation matrices, General data matrices
*   💭 **Bubble Charts:** Three variables (X, Y, Size)
*   🌳 **Tree Maps:** Hierarchical/Proportional area
*   📉 **Control Charts:** X-bar (Averages), P-chart (Proportions)
*   🗺️ **World Map:** (Attempted via `geopandas`)

## Key Findings & Observations (from `datavisualization_project_1.ipynb`)

*   Visual comparisons suggest potential differences in average scores between genders across math, reading, and writing.
*   Completing the test preparation course appears to be associated with higher average scores.
*   Parental level of education shows some variation in relation to average math scores.
*   The type of lunch (standard vs. free/reduced) seems to correlate with differences in writing score distributions.
*   Reading and writing scores exhibit a strong positive correlation.

*(Note: These are visual observations from the plots; formal statistical testing would be needed for definitive conclusions.)*

## Datasets

*   **`StudentsPerformance.csv`**: This is the primary dataset used in `datavisualization_project_1.ipynb`. **You must ensure this file is available and the path within the notebook is correct.**
*   Other notebooks (`different_chart_of_visualization.ipynb`, `controlchart_class_lab.ipynb`) use sample data defined directly in the code.

## Prerequisites

To run these notebooks, you need Python 3 and the following libraries:

*   pandas
*   numpy
*   matplotlib
*   seaborn
*   squarify
*   geopandas (Optional - for the world map example which might require extra setup)

You can install these packages using pip:
```bash
pip install pandas numpy matplotlib seaborn squarify geopandas
