Add-Type -AssemblyName System.IO.Compression.FileSystem
$path = "C:\Users\LENOVO\Desktop\Laboratory 10  Retrofit  (1).docx"
$zip = [System.IO.Compression.ZipFile]::OpenRead($path)
foreach ($entry in $zip.Entries) {
    if ($entry.FullName -eq "word/document.xml") {
        $reader = New-Object System.IO.StreamReader($entry.Open())
        $xml = [xml]$reader.ReadToEnd()
        $namespaces = @{w = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"}
        $nodes = Select-Xml -Xml $xml -XPath "//w:p" -Namespace $namespaces
        foreach ($node in $nodes) {
            $texts = Select-Xml -Xml $node.Node -XPath ".//w:t" -Namespace $namespaces
            $line = ($texts | ForEach-Object { $_.Node.InnerText }) -join ""
            if ($line) { [Console]::WriteLine($line) }
        }
    }
}
$zip.Dispose()
